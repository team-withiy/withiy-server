package com.server.domain.course.dto;

import com.server.domain.category.dto.CategoryDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursePlaceDto {
    private CategoryDto category;
    private String address;
}
