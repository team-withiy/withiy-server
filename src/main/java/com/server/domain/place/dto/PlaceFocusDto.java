package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceFocusDto {
    private Long id;
    private String name;
    private CategoryDto category;
}
