package com.server.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private Long categoryId;
}
