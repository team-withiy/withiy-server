package com.server.domain.route.dto;

import com.server.domain.photo.entity.Photo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoInRouteDto {

	private Long photoId;
	private String photoUrl;
	private UploaderDto uploader;

	public static PhotoInRouteDto from(Photo photo) {
		return PhotoInRouteDto.builder()
			.photoId(photo.getId())
			.photoUrl(photo.getImgUrl())
			.uploader(UploaderDto.from(photo.getUser()))
			.build();
	}

}
