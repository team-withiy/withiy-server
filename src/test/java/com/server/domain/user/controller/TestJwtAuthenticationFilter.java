package com.server.domain.user.controller;

import com.server.domain.user.entity.User;
import com.server.global.jwt.JwtAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Test-specific JWT authentication filter that creates authentication based on Authorization
 * header
 */
@Slf4j
public class TestJwtAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		List<GrantedAuthority> authorities = new ArrayList<>();

		// If security context already has authentication (from the RequestPostProcessor), use that
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
		if (existingAuth != null && existingAuth.isAuthenticated()) {
			log.info("Using existing authentication: {}", existingAuth.getName());
			filterChain.doFilter(request, response);
			return;
		}

		// Otherwise, check if the Authorization header contains a JWT token
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			log.info("Found Bearer token in header: {}", authHeader);

			// For tests, we don't validate the token - just having it is enough
			// Instead, we create a mock User and authorities
			User mockUser = new User();
			ReflectionTestUtils.setField(mockUser, "id", 1L);
			ReflectionTestUtils.setField(mockUser, "nickname", "testUser");
			ReflectionTestUtils.setField(mockUser, "admin", false);

			// Assign USER role by default
			authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

			// Create and set authentication
			Authentication auth = new JwtAuthentication(mockUser, authorities);
			SecurityContextHolder.getContext().setAuthentication(auth);

			log.info("Set up test authentication for user: {}", mockUser.getNickname());
		} else {
			log.info("No Authorization header found or not a Bearer token");
			// Set anonymous role if no Authorization header
			authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
			SecurityContextHolder.getContext()
				.setAuthentication(new JwtAuthentication(null, authorities));
		}

		filterChain.doFilter(request, response);
	}
}
