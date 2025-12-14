package com.server.domain.photo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.server.domain.photo.entity.Photo;
import com.server.domain.user.dto.SimpleUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotoDto {

	@Schema(description = "사진 ID", example = "12345")
	private Long photoId;
	@Schema(description = "이미지 URL", example = "https://cdn.example.com/photo/12345.jpg")
	private String imageUrl;
	@Schema(description = "업로더 정보")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private SimpleUserDto uploader;

	@Builder
	public PhotoDto(Long photoId, String imageUrl, SimpleUserDto uploader) {
		this.photoId = photoId;
		this.imageUrl = imageUrl;
		this.uploader = uploader;
	}

	public static PhotoDto from(Photo photo) {
		return PhotoDto.builder()
			.uploader(SimpleUserDto.from(photo.getUser()))
			.photoId(photo.getId())
			.imageUrl(photo.getImgUrl())
			.build();
	}
}
