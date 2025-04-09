package com.server.domain.category.dto;

import com.server.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;

    // Category 엔티티에서 필요한 정보만 추출
    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

}
