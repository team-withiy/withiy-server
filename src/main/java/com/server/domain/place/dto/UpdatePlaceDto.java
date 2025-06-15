package com.server.domain.place.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePlaceDto {
    private String name;
    private String address;
    private String region1depth;
    private String region2depth;
    private String region3depth;
    private String latitude;
    private String longitude;
    private Long score;
    private String category;
}
