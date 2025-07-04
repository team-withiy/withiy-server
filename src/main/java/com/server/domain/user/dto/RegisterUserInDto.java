package com.server.domain.user.dto;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterUserInDto {
    private Map<Long, Boolean> termAgreements; // Key: Term ID, Value: Agreed or not
    private String nickname; // 사용자 닉네임
    private String thumbnail; // 프로필 이미지 URL
}
