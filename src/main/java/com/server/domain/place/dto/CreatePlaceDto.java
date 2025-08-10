package com.server.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePlaceDto {

	@Schema(description = "장소 이름", example = "서울숲")
	private String name;
	@Schema(description = "장소 주소", example = "서울특별시 성동구 서울숲길 273")
	private String address;
	@Schema(description = "장소 지역 1단계", example = "서울특별시")
	private String region1depth;
	@Schema(description = "장소 지역 2단계", example = "성동구")
	private String region2depth;
	@Schema(description = "장소 지역 3단계", example = "서울숲동")
	private String region3depth;
	@Schema(description = "장소 위도", example = "37.5432")
	private String latitude;
	@Schema(description = "장소 경도", example = "127.0423")
	private String longitude;
	@Schema(description = "장소 카테고리 이름", example = "데이트 코스")
	private String categoryName;
	@Schema(description = "장소 이미지 URL 목록", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
	private List<String> imageUrls;
}
