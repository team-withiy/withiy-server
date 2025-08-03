package com.server.domain.category.dto;

import com.server.domain.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    @Schema(description = "카테고리 고유 ID", example = "1")
    private Long id;
    @Schema(description = "카테고리 이름", example = "데이트 코스")
    private String name;
    @Schema(description = "카테고리 아이콘 URL", example = "https://example.com/icon.png")
    private String icon;


    public static CategoryDto from(Category category){
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .build();
    }



}
