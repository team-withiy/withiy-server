package com.server.domain.photo.dto;

import com.server.domain.photo.entity.Photo;
import com.server.domain.place.dto.PlaceDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoDto {
    private String imgUrl;


    public static PhotoDto from(Photo photo){
        return PhotoDto.builder()
                .imgUrl(photo.getImgUrl())
                .build();
    }
}
