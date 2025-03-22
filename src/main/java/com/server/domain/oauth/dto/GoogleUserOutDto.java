package com.server.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserOutDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("email")
    private String email;

    // @JsonProperty("verified_email")
    // private bool verifiedEmail;

    @JsonProperty("name")
    private String name;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("picture")
    private String picture;
}
