package com.server.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthTokenOutDto {

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("expires_in")
	private Long accessTokenExp;
}
