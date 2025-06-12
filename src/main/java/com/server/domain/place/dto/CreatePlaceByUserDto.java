package com.server.domain.place.dto;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.entity.Photo;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePlaceByUserDto {
    private List<PhotoDto> photos;
    private String placeName;
    private String address;
    private String latitude;
    private String longitude;
    private String category;
    private Long score;
    private String review;

}
