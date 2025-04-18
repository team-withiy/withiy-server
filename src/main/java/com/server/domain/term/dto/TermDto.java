
package com.server.domain.term.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.server.domain.term.entity.Term;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermDto {
    private Long id;
    private String title;
    private List<String> content;
    private boolean required;

    public static TermDto from(Term term) {
        return TermDto.builder()
                .id(term.getId())
                .title(term.getTitle())
                .content(Arrays.stream(term.getContent().split("\\|\\|")).collect(Collectors.toList()))
                .required(term.isRequired())
                .build();
    }
}
