package com.server.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService {

	private static final String BEARER = "Bearer ";
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;
	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	private SecretKey getSigningKey() {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String createAccessToken(Long userId) {
		return createToken(String.valueOf(userId), accessTokenExpiration);
	}

	public String createRefreshToken(Long userId) {
		return createToken(String.format("%s.refresh", userId), refreshTokenExpiration);
	}

	private String createToken(String sub, long exp) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + exp);
		String token = Jwts.builder()
			.setSubject(sub)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(getSigningKey(), SignatureAlgorithm.HS512)
			.compact();
		log.info("Created token for user: {}, token: {}", sub, token);
		return token;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			log.info("Token is valid");
			return true;
		} catch (Exception e) {
			log.error("Invalid JWT token: {}", e.getMessage());
			return false;
		}
	}

	public Optional<Long> extractUserId(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
			String userId = claims.getSubject();
			if (userId != null && userId.endsWith(".refresh")) {
				userId = userId.substring(0, userId.indexOf(".refresh"));
			}
			log.info("Extracted username from token: {}", userId);
			return Optional.ofNullable(Long.valueOf(userId));
		} catch (Exception e) {
			log.error("Failed to extract username from token: {}", e.getMessage());
			return Optional.empty();
		}
	}

	public Optional<String> extractAccessToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		// ✅ 헤더가 없으면 로그 찍지 말고 빠르게 리턴
		if (authHeader == null || authHeader.isBlank()) {
			return Optional.empty();
		}
		if (authHeader.startsWith(BEARER)) {
			String token = authHeader.substring(BEARER.length()).trim();
			log.debug("Extracted access token: {}", token); // debug로 낮춰도 충분
			return Optional.of(token);
		}

		return Optional.empty();
	}

	public Optional<Long> extractUserId(HttpServletRequest request) {
		Optional<String> accessToken = extractAccessToken(request);
		if (accessToken.isEmpty()) {
			log.warn("Access token is empty");
			return Optional.empty();
		}
		if (!validateToken(accessToken.get())) {
			log.warn("Token is invalid");
			return Optional.empty();
		}
		return extractUserId(accessToken.get());
	}
}
