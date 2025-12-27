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
public class ReviewPhotoDto {

	private Long photoId;
	private String photoUrl;

	public static ReviewPhotoDto from(Photo photo) {
		return ReviewPhotoDto.builder()
			.photoId(photo.getId())
			.photoUrl(photo.getImgUrl())
			.build();
	}

}
