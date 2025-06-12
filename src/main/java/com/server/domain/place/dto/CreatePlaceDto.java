package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePlaceDto {
    private String name;
    private String address;
    private String region1depth;
    private String region2depth;
    private String region3depth;
    private String latitude;
    private String longitude;
    private String category;

}
