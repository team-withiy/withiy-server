package com.server.domain.oauth.dto;

import static com.server.global.error.code.AuthErrorCode.ILLEGAL_REGISTRATION_ID;

import com.server.domain.oauth.entity.OAuth;
import com.server.domain.term.entity.Term;
import com.server.domain.user.entity.User;
import com.server.global.error.exception.AuthException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Builder
@Slf4j
public class OAuth2UserInfo {

	// 생성되는 코드의 길이 (바이트 단위)
	private static final int CODE_LENGTH = 16;
	private static final SecureRandom RANDOM = new SecureRandom();
	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String nickname;
	private String email;
	private String picture;
	private String provider;
	private String providerId;

	// URI-safe한 랜덤 문자열을 생성하는 메서드
	private static String generateRandomCode() {
		byte[] randomBytes = new byte[CODE_LENGTH];
		RANDOM.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}

	public static OAuth2UserInfo of(String registrationId, String nameAttributeKey,
		Map<String, Object> attributes) {
		return switch (registrationId) { // registration id별로 userInfo 생성
			case "google" -> ofGoogle(registrationId, nameAttributeKey, attributes);
			case "naver" -> ofNaver(registrationId, nameAttributeKey, attributes);
			case "kakao" -> ofKakao(registrationId, nameAttributeKey, attributes);
			default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
		};
	}

	private static OAuth2UserInfo ofGoogle(String registrationId, String nameAttributeKey,
		Map<String, Object> attributes) {
		return OAuth2UserInfo.builder().nameAttributeKey(nameAttributeKey)
			.nickname((String) attributes.get("name")).email((String) attributes.get("email"))
			.picture((String) attributes.get("picture")).provider(registrationId)
			.attributes(attributes).providerId((String) attributes.get("sub")).build();
	}

	private static OAuth2UserInfo ofNaver(String registrationId, String nameAttributeKey,
		Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		return OAuth2UserInfo.builder().nameAttributeKey(nameAttributeKey)
			.nickname((String) response.get("nickname")).email((String) response.get("email"))
			.picture((String) response.get("profile_image")).provider(registrationId)
			.attributes(attributes).providerId((String) response.get("id")).build();
	}

	private static OAuth2UserInfo ofKakao(String registrationId, String nameAttributeKey,
		Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

		String providerId = String.valueOf(attributes.get("id"));

		String email =
			kakaoAccount.containsKey("email") ? (String) kakaoAccount.get("email") : null;

		return OAuth2UserInfo.builder().nameAttributeKey(nameAttributeKey)
			.nickname((String) profile.get("nickname")).email(email)
			.picture((String) profile.get("profile_image_url")).provider(registrationId)
			.attributes(attributes).providerId(providerId).build();

	}

	public OAuth toEntity(List<Term> terms) {
		// 사용자 고유 코드 생성
		String randomCode = generateRandomCode();
		log.info("새 사용자를 위한 랜덤 코드 생성: {}", randomCode);

		User user =
			User.builder()
				.nickname(nickname).thumbnail(picture).code(randomCode) // 생성된 랜덤 코드 설정
				.build();

		return OAuth.builder().provider(provider)
			.providerId(providerId).email(email).nickname(nickname).thumbnail(picture).user(user)
			.build();
	}
}
