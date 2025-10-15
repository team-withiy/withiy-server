package com.server.global.config;

import com.server.domain.oauth.handler.OAuth2SuccessHandler;
import com.server.domain.oauth.service.OAuth2UserService;
import com.server.global.error.handler.FilterChainExceptionHandler;
import com.server.global.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.server.global.jwt")
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final OAuth2UserService oAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final FilterChainExceptionHandler filterChainExceptionHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// ✅ 요청 경로별 접근 권한 설정
			.authorizeHttpRequests(auth -> auth
				// Swagger & Docs
				.requestMatchers(
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/v3/api-docs/**",
					"/v3/api-docs.yaml"
				).permitAll()
				// 에러, favicon 허용
				.requestMatchers("/error", "/favicon.ico").permitAll()
				// 인증 관련 및 OAuth2 로그인 허용
				.requestMatchers("/auth/**", "/oauth2/**").permitAll()
				// API (임시로 공개, 운영 시 제한 예정)
				.requestMatchers("/api/**").permitAll()
				// 나머지는 인증 필요
				.anyRequest().authenticated()
			)

			// ✅ 세션 비활성화 (JWT 기반)
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			// ✅ 불필요한 인증 수단 비활성화
			.csrf(csrf -> csrf.disable())
			.httpBasic(httpBasic -> httpBasic.disable())
			.formLogin(form -> form.disable())

			// ✅ OAuth2 로그인 설정
			.oauth2Login(oauth -> oauth
				.userInfoEndpoint(user -> user.userService(oAuth2UserService))
				.successHandler(oAuth2SuccessHandler)
			)

			// ✅ 필터 순서
			.addFilterBefore(jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(filterChainExceptionHandler,
				JwtAuthenticationFilter.class);    // JWT 필터 다음

		return http.build();
	}
}
