package com.server.domain.user.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.server.domain.term.dto.TermAgreementDto;
import com.server.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {
    public String nickname;
    public String thumbnail;
    private List<TermAgreementDto> termAgreement;

    public static UserDto from(User user) {
        return UserDto.builder()
                .nickname(user.getNickname())
                .thumbnail(user.getThumbnail())
                .termAgreement(user.getTermAgreements().stream()
                        .map(termAgreement -> TermAgreementDto.from(termAgreement))
                        .collect(Collectors.toList()))
                .build();
    }
}
