package com.server.domain.photo.dto;

import com.server.domain.photo.entity.Photo;
import com.server.domain.user.dto.SimpleUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotoDto {

	@Schema(description = "이미지 URL", example = "https://cdn.example.com/photo/12345.jpg")
	private String imageUrl;
	@Schema(description = "업로더 정보")
	private SimpleUserDto uploader;

	@Builder
	public PhotoDto(String imageUrl, SimpleUserDto uploader) {
		this.imageUrl = imageUrl;
		this.uploader = uploader;
	}

	public static PhotoDto from(Photo photo) {
		return PhotoDto.builder()
			.uploader(SimpleUserDto.from(photo.getUser()))
			.imageUrl(photo.getImgUrl())
			.build();
	}
}
