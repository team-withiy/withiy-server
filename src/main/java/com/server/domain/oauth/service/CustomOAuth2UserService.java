package com.server.domain.oauth.service;

import com.server.domain.oauth.dto.OAuth2UserInfo;
import com.server.domain.oauth.dto.PrincipalDetails;
import com.server.domain.oauth.entity.OAuth;
import com.server.domain.oauth.repository.OAuthRepository;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final OAuthRepository oAuthRepository;

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
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        // 5. 회원가입 및 로그인
        OAuth oAuth = getOrSave(oAuth2UserInfo);

        // 6. OAuth2User로 반환
        return new PrincipalDetails(oAuth.getUser(), oAuth2UserAttributes, userNameAttributeName);
    }

    private OAuth getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        OAuth oAuth = oAuthRepository.findByProviderAndProviderId(oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId())
                .orElseGet(oAuth2UserInfo::toEntity);
        log.info(oAuth.getUser().getNickname());
        return oAuthRepository.save(oAuth);
    }
}
