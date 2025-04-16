package com.server.domain.oauth.service;

import java.util.List;
import java.util.Map;

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

@RequiredArgsConstructor
@Service
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final OAuthRepository oAuthRepository;
    private final TermRepository termRepository;
    private final TermAgreementRepository termAgreementRepository;

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

        // 6. OAuth2User로 반환
        return new PrincipalDetails(oAuth.getUser(), oAuth2UserAttributes, userNameAttributeName);
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
