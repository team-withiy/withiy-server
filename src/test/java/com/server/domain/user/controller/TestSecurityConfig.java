package com.server.domain.user.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.MethodParameter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity
public class TestSecurityConfig implements WebMvcConfigurer {

	@Bean
	@Primary
	public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).sessionManagement(
				session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

	@Bean
	@Primary
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username)
				throws UsernameNotFoundException {
				if ("testUser".equals(username)) {
					return User.withUsername("testUser").password("password")
						.authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
						.build();
				}
				throw new UsernameNotFoundException("User not found: " + username);
			}
		};
	}

	/**
	 * AuthenticationPrincipal 어노테이션 처리를 위한 ArgumentResolver를 등록
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new TestUserArgumentResolver());
	}

	/**
	 * 테스트용 AuthenticationPrincipal 어노테이션 처리기
	 */
	static class TestUserArgumentResolver implements HandlerMethodArgumentResolver {

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return parameter.getParameterType().equals(com.server.domain.user.entity.User.class)
				&& parameter.hasParameterAnnotation(
				org.springframework.security.core.annotation.AuthenticationPrincipal.class);
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.getPrincipal() instanceof com.server.domain.user.entity.User) {
				return auth.getPrincipal();
			}

			// 기본 테스트 사용자 생성
			com.server.domain.user.entity.User testUser = new com.server.domain.user.entity.User();
			ReflectionTestUtils.setField(testUser, "id", 1L);
			ReflectionTestUtils.setField(testUser, "nickname", "testUser");
			ReflectionTestUtils.setField(testUser, "thumbnail", "thumbnail.jpg");
			ReflectionTestUtils.setField(testUser, "code", "USER123");
			ReflectionTestUtils.setField(testUser, "isAdmin", false);

			return testUser;
		}
	}
}
