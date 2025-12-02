package com.server.domain.bookmark.dto;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.place.entity.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkedPlaceDto {

	@Schema(description = "장소 ID", example = "1")
	private Long id;
	@Schema(description = "장소 이름", example = "홍대입구역")
	private String name;
	@Schema(description = "장소 주소", example = "서울특별시 마포구 양화로 123")
	private String address;
	@Schema(description = "장소 온도", example = "75")
	private Double score;
	@Schema(description = "장소 사진 목록")
	private List<PhotoDto> photos;

	public static BookmarkedPlaceDto of(Place place, Double score, List<PhotoDto> photos) {
		return BookmarkedPlaceDto.builder()
			.id(place.getId())
			.name(place.getName())
			.address(place.getAddress())
			.score(score)
			.photos(photos)
			.build();
	}
}
