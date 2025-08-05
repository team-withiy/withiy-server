package com.server.domain.photo.dto;

import com.server.domain.photo.entity.Photo;
import com.server.domain.user.dto.SimpleUserDto;
import lombok.*;

@Getter
@NoArgsConstructor
public class PhotoDto {
    private String imageUrl;
    private SimpleUserDto uploader;

    @Builder
    public PhotoDto(String imageUrl, SimpleUserDto uploader) {
        this.imageUrl = imageUrl;
        this.uploader = uploader;
    }

    public static PhotoDto from(Photo photo){
        return PhotoDto.builder()
            .uploader(SimpleUserDto.from(photo.getUser()))
            .imageUrl(photo.getImgUrl())
            .build();
    }
}
