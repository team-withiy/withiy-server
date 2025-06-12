package com.server.domain.place.dto;

import com.server.domain.photo.dto.PhotoDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterPlaceDto {
    private List<PhotoDto> photos;
    private Long placeId;
    private Long score;
    private String review;
}
