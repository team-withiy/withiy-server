package com.server.domain.oauth.service;

import java.util.List;
import java.util.Map;

import com.server.domain.user.entity.User;
import com.server.global.error.code.ProfileErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.service.ImageService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.server.domain.oauth.dto.OAuth2UserInfo;
import com.server.domain.oauth.dto.PrincipalDetails;
import com.server.domain.oauth.entity.OAuth;
import com.server.domain.oauth.repository.OAuthRepository;
import com.server.domain.term.entity.TermAgreement;
import com.server.domain.term.repository.TermAgreementRepository;
import com.server.domain.term.repository.TermRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final OAuthRepository oAuthRepository;
    private final TermRepository termRepository;
    private final TermAgreementRepository termAgreementRepository;
    private final ImageService imageService;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // 2. resistrationId 가져오기 (third-party id)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, userNameAttributeName, oAuth2UserAttributes);

        // 5. 회원가입 및 로그인
        OAuth oAuth = getOrSave(oAuth2UserInfo);
        User user = oAuth.getUser();

        // 소셜 프로필 사진이 있는 경우 S3에 업로드
        String pictureUrl = oAuth2UserInfo.getPicture();
        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            // 소셜 프로필 사진 다운로드
            MultipartFile file = imageService.downloadImage(pictureUrl);
            // 사진을 S3에 업로드
            if (file == null || file.isEmpty()) {
                log.warn("다운로드 이미지 파일이 비어 있습니다: {}", pictureUrl);
                throw new BusinessException(ProfileErrorCode.DOWNLOAD_PROFILE_IMAGE_EMPTY);
            }
            String imageUrl = imageService.uploadImage(file, "user", user.getId()).getImageUrl();
            log.info("변경된 프로필 사진 URL: {}", imageUrl);
            // S3 에 업로드된 프로필 사진 URL 설정
            user.setThumbnail(imageUrl);
        }

        // 6. OAuth2User로 반환
        return new PrincipalDetails(user, oAuth2UserAttributes, userNameAttributeName);
    }

    private OAuth getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        try {
            log.info("Provider: {}, ProviderId: {}", oAuth2UserInfo.getProvider(),
                    oAuth2UserInfo.getProviderId());

            OAuth oAuth = oAuthRepository.findByProviderAndProviderId(oAuth2UserInfo.getProvider(),
                    oAuth2UserInfo.getProviderId()).orElseGet(() -> {
                        log.info("새 사용자 등록: {}", oAuth2UserInfo.getNickname());
                        return oAuth2UserInfo.toEntity(termRepository.findAll());
                    });

            log.info("사용자 정보: {}", oAuth.getUser().getNickname());
            oAuth = oAuthRepository.save(oAuth);
            List<TermAgreement> termAgreements = oAuth.getUser().getTermAgreements();
            for (TermAgreement termAgreement : termAgreements) {
                termAgreementRepository.save(termAgreement);
            }
            return oAuth;
        } catch (Exception e) {
            log.error("사용자 저장 중 오류: {}", e.getMessage(), e);
            throw e;
        }
    }

}
