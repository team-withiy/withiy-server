package com.server.global.jwt;

import com.server.domain.user.entity.User;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthentication extends AbstractAuthenticationToken {

	private final User principal;

	public JwtAuthentication(User principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return this.principal;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}
}
