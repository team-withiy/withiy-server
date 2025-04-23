package com.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.server.domain.oauth.handler.OAuth2SuccessHandler;
import com.server.domain.oauth.service.OAuth2UserService;
import com.server.global.error.handler.FilterChainExceptionHandler;
import com.server.global.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

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
        public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
                return web -> web.ignoring()
                                // error endpoint를 열어줘야 함, favicon.ico 추가!
                                .requestMatchers("/error", "/favicon.ico");
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(auth -> auth
                                // Auth
                                .requestMatchers("/auth/**", "/oauth2/**").permitAll()
                                // API
                                .requestMatchers("/api/**").permitAll()
                                // Swagger
                                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger")
                                .permitAll().anyRequest().authenticated())
                                .csrf(csrf -> csrf.disable())
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .formLogin(form -> form.disable())
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))
                                .oauth2Login(oauth -> oauth.userInfoEndpoint(
                                                c -> c.userService(oAuth2UserService))
                                                .successHandler(oAuth2SuccessHandler))
                                // 예외 처리 필터를 가장 먼저 추가
                                .addFilterBefore(filterChainExceptionHandler,
                                                UsernamePasswordAuthenticationFilter.class)
                                // JWT 인증 필터는 그 다음에 추가
                                .addFilterBefore(jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }
}
