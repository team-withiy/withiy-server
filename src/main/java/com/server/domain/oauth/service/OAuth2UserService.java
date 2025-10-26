package com.server.domain.oauth.service;

import com.server.domain.oauth.dto.OAuth2UserInfo;
import com.server.domain.oauth.dto.PrincipalDetails;
import com.server.domain.oauth.entity.OAuth;
import com.server.domain.oauth.repository.OAuthRepository;
import com.server.domain.term.entity.TermAgreement;
import com.server.domain.term.repository.TermAgreementRepository;
import com.server.domain.term.repository.TermRepository;
import com.server.domain.user.entity.User;
import com.server.global.service.ImageService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
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
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, userNameAttributeName,
			oAuth2UserAttributes);

		// 5. 회원가입 및 로그인
		OAuth oAuth = getOrSave(oAuth2UserInfo);
		User user = oAuth.getUser();

		// 6. OAuth2User로 반환
		return new PrincipalDetails(user, oAuth2UserAttributes, userNameAttributeName);
	}

	private OAuth getOrSave(OAuth2UserInfo oAuth2UserInfo) {
		try {
			log.info("Provider: {}, ProviderId: {}", oAuth2UserInfo.getProvider(),
				oAuth2UserInfo.getProviderId());

			Optional<OAuth> optionalOAuth = oAuthRepository.findByProviderAndProviderId(
				oAuth2UserInfo.getProvider(),
				oAuth2UserInfo.getProviderId());

			OAuth oAuth;
			if (optionalOAuth.isPresent()) {
				oAuth = optionalOAuth.get();
			} else {
				log.info("새 사용자 등록: {}", oAuth2UserInfo.getNickname());
				oAuth = oAuth2UserInfo.toEntity(termRepository.findAll());
				User newUser = oAuth.getUser();

				// 프로필 이미지가 있는 경우에만 처리
				String pictureUrl = oAuth2UserInfo.getPicture();
				if (pictureUrl != null && !pictureUrl.isBlank()) {
					MultipartFile file = imageService.downloadImage(pictureUrl);
					String imageUrl = imageService.uploadImage(file, "user", newUser.getId())
						.getImageUrl();
					oAuth.updateThumbnail(imageUrl);
					newUser.updateThumbnail(imageUrl);
				}
				oAuthRepository.save(oAuth);
			}

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
