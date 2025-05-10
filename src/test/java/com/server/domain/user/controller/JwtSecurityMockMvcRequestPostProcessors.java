package com.server.domain.user.controller;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.server.domain.user.entity.User;
import com.server.global.jwt.JwtAuthentication;

/**
 * Custom security utilities for JWT authentication in tests
 */
public class JwtSecurityMockMvcRequestPostProcessors {

    /**
     * Create a RequestPostProcessor that uses a JWT Authentication with the provided user
     */
    public static RequestPostProcessor jwt(User user) {
        return request -> {
            SecurityContext context = SecurityContextHolder.getContext();
            Collection<GrantedAuthority> authorities =
                    user.isAdmin() ? AuthorityUtils.createAuthorityList("ROLE_ADMIN")
                            : AuthorityUtils.createAuthorityList("ROLE_USER");

            Authentication auth = new JwtAuthentication(user, authorities);
            context.setAuthentication(auth);

            // Also apply spring security's CSRF protection
            request = SecurityMockMvcRequestPostProcessors.csrf().postProcessRequest(request);

            return request;
        };
    }
}
