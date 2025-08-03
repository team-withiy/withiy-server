package com.server.domain.place.dto;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.entity.Photo;
import jakarta.persistence.Column;
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
    private String region1depth;
    private String region2depth;
    private String region3depth;
    private String latitude;
    private String longitude;
    private String category;
    private String review;
    private Long score;

}
