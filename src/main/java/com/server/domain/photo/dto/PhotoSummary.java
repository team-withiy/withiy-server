package com.server.domain.photo.dto;

import com.server.domain.photo.entity.Photo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoSummary {

	private Long photoId;
	private String imageUrl;

	public static PhotoSummary from(Photo photo) {
		return PhotoSummary.builder()
			.photoId(photo.getId())
			.imageUrl(photo.getImgUrl())
			.build();
	}
}
