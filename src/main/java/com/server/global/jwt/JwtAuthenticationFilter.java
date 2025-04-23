package com.server.global.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.exception.BusinessException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        Optional<String> token = jwtService.extractAccessToken(request);
        List<GrantedAuthority> authorities = new ArrayList<>();
        User user;
        if (token.isEmpty()) {
            authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
            user = null;
        } else {
            Long userId = jwtService.extractUserId(token.get())
                    .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_ACCESS_TOKEN));
            user = userService.getUserWithPersonalInfo(userId);
            if (user.isAdmin()) {
                authorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN");
            } else {
                authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }
            log.info("Nickname: {}, Thumbnail: {}", user.getNickname(), user.getThumbnail());
        }
        Authentication auth = new JwtAuthentication(user, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
