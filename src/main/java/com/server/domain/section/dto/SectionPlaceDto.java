package com.server.domain.section.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.section.entity.SectionPlace;
import lombok.*;

import static org.antlr.v4.runtime.misc.Utils.sequence;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionPlaceDto {
    private Long id;
    private String name;
    private String thumbnail;
    private String address;
    private String latitude;
    private String longitude;
    private CategoryDto category;
    private Long score;
    private int order;


}
