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
    private String nickname;
    private String thumbnail;
    private Boolean restoreEnabled;
    private List<TermAgreementDto> termAgreement;

    public static UserDto from(User user) {
        Boolean restoreEnabled;
        if (user.getDeletedAt() != null) {
            restoreEnabled = true;
        } else {
            restoreEnabled = false;
        }
        return UserDto.builder()
                .nickname(user.getNickname())
                .thumbnail(user.getThumbnail())
                .restoreEnabled(restoreEnabled)
                .termAgreement(user.getTermAgreements().stream()
                        .map(termAgreement -> TermAgreementDto.from(termAgreement))
                        .collect(Collectors.toList()))
                .build();
    }
}
