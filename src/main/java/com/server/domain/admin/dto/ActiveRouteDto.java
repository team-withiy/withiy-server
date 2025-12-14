package com.server.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ActiveRouteDto {

	@Schema(description = "루트 고유 ID", example = "1")
	private Long routeId;
	@Schema(description = "루트 이름", example = "서울 데이트 코스")
	private String routeName;
	@Schema(description = "루트에 포함된 장소 이름 목록", example = "[\"서울숲\", \"한강공원\"]")
	private List<String> placeNames;
	@Schema(description = "북마크 수", example = "150")
	private Long bookmarkCount;
	@Schema(description = "루트 사진 URL 목록", example = "[\"https://example.com/route1.jpg\", \"https://example.com/route2.jpg\"]")
	private List<String> photoUrls;

	@Builder
	public ActiveRouteDto(Long routeId, String routeName, List<String> placeNames,
		Long bookmarkCount, List<String> photoUrls) {
		this.routeId = routeId;
		this.routeName = routeName;
		this.placeNames = placeNames;
		this.bookmarkCount = bookmarkCount;
		this.photoUrls = photoUrls;
	}
}
