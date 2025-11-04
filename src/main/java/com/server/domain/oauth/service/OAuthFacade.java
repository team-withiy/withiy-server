package com.server.domain.oauth.service;

import com.server.domain.oauth.dto.OAuth2UserInfo;
import com.server.domain.oauth.entity.OAuth;
import com.server.domain.oauth.repository.OAuthRepository;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.service.ImageService;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class OAuthFacade {

	private final OAuthRepository oAuthRepository;
	private final UserService userService;
	private final ImageService imageService;

	private static final int CODE_LENGTH = 16;
	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * Provider와 ProviderId를 기준으로 OAuth 정보 조회. 없으면 새 User + OAuth 등록.
	 */
	@Transactional
	public OAuth getOrSave(OAuth2UserInfo userInfo) {
		log.info("[OAuthFacadeService] provider={}, providerId={}",
			userInfo.getProvider(), userInfo.getProviderId());

		return oAuthRepository.findByProviderAndProviderId(
				userInfo.getProvider(), userInfo.getProviderId())
			.orElseGet(() -> registerNewOAuth(userInfo));
	}

	/**
	 * 신규 OAuth 및 User 등록 로직
	 */
	private OAuth registerNewOAuth(OAuth2UserInfo userInfo) {
		// ✅ 1. User 생성 및 저장
		User user = userService.saveUser(
			User.builder()
				.nickname(userInfo.getNickname())
				.thumbnail(userInfo.getPicture())
				.code(generateRandomCode())
				.build()
		);

		log.info("[OAuthFacadeService] 새 사용자 등록: {}", user.getNickname());

		// ✅ 2. OAuth 엔티티 생성 (User 연관관계 포함)
		OAuth oAuth = userInfo.toEntity(user);

		// ✅ 3. 프로필 이미지가 있는 경우 S3 등 업로드 처리
		updateProfileImageIfExists(user, oAuth, userInfo.getPicture());

		// ✅ 4. OAuth 영속화
		return oAuthRepository.save(oAuth);
	}

	/**
	 * 프로필 이미지가 존재할 경우 다운로드 후 업로드하여 경로 갱신
	 */
	private void updateProfileImageIfExists(User user, OAuth oAuth, String pictureUrl) {
		if (pictureUrl == null || pictureUrl.isBlank()) {
			return;
		}

		MultipartFile file = imageService.downloadImage(pictureUrl);
		String uploadedUrl = imageService.uploadImage(file, "user", user.getId()).getImageUrl();

		user.updateThumbnail(uploadedUrl);
		oAuth.updateThumbnail(uploadedUrl);
	}

	/**
	 * URI-safe한 랜덤 문자열 생성
	 */
	private static String generateRandomCode() {
		byte[] randomBytes = new byte[CODE_LENGTH];
		RANDOM.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}
}
