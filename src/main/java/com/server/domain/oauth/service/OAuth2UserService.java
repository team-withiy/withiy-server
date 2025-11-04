package com.server.domain.oauth.service;

import com.server.domain.oauth.dto.OAuth2UserInfo;
import com.server.domain.oauth.dto.PrincipalDetails;
import com.server.domain.oauth.entity.OAuth;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

	private final OAuthFacade oAuthFacade;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// ✅ 1. Provider에서 사용자 정보(attributes) 가져오기
		Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();

		// ✅ 2. registrationId, userNameAttributeName 추출
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

		// ✅ 3. OAuth2UserInfo DTO 생성 (provider별 표준화)
		OAuth2UserInfo userInfo = OAuth2UserInfo.of(registrationId, userNameAttributeName,
			attributes);

		// ✅ 4. DB에 OAuth 및 User 등록 or 기존 사용자 조회
		OAuth oAuth = oAuthFacade.getOrSave(userInfo);

		// ✅ 5. Spring Security용 PrincipalDetails 반환
		return new PrincipalDetails(oAuth.getUser(), attributes, userNameAttributeName);
	}
}
