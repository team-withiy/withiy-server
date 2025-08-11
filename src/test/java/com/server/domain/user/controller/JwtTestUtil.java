package com.server.domain.user.controller;

import com.server.domain.user.entity.User;
import com.server.global.jwt.JwtAuthentication;
import com.server.global.jwt.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * JWT 관련 테스트를 위한 유틸리티 클래스
 */
public class JwtTestUtil {

	/**
	 * 테스트용 JWT 토큰을 생성하고 요청 헤더에 추가하는 RequestPostProcessor 생성
	 *
	 * @param jwtService JWT 서비스
	 * @param user       유저 정보
	 * @return JWT 토큰이 포함된 RequestPostProcessor
	 */
	public static RequestPostProcessor withJwt(JwtService jwtService, User user) {
		return mockRequest -> {
			// 토큰 생성 및 Authorization 헤더에 추가
			String token = jwtService.createAccessToken(user.getId());
			mockRequest.addHeader("Authorization", "Bearer " + token);

			// SecurityContext에 인증 정보 설정
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			Authentication auth = new JwtAuthentication(user,
				user.isAdmin() ? AuthorityUtils.createAuthorityList("ROLE_ADMIN")
					: AuthorityUtils.createAuthorityList("ROLE_USER"));
			context.setAuthentication(auth);
			SecurityContextHolder.setContext(context);

			// 테스트용 특별 헤더 추가
			mockRequest.addHeader("X-Test-User-Id", user.getId().toString());
			mockRequest.addHeader("X-Test-Auth", "true");

			return mockRequest;
		};
	}

	/**
	 * 가짜 JWT 토큰을 생성하여 요청 헤더에 추가하는 RequestPostProcessor 생성 JwtService가 모킹되어 있을 때 사용
	 *
	 * @return 가짜 JWT 토큰이 포함된 RequestPostProcessor
	 */
	public static RequestPostProcessor withMockJwt() {
		return mockRequest -> {
			mockRequest.addHeader("Authorization", "Bearer mock-jwt-token");
			return mockRequest;
		};
	}
}
