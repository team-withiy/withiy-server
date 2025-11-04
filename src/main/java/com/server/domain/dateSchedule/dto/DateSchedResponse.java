package com.server.domain.dateSchedule.dto;

import com.server.domain.dateSchedule.entity.DateSchedule;
import com.server.domain.photo.entity.Photo;
import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceStatus;
import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RoutePlace;
import com.server.domain.route.entity.RouteStatus;
import com.server.domain.route.entity.RouteType;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DateSchedResponse {

	@Schema(description = "일정 ID")
	private Long id;

	@Schema(description = "일정 이름")
	private String name;

	@Schema(description = "장소 정보")
	private List<PlaceDto> places;

	@Schema(description = "경로 상태")
	private RouteStatus status;

	@Schema(description = "경로 유형")
	private RouteType routeType;

	@Schema(description = "일정 썸네일")
	private String thumbnail;

	@Data
	@Builder
	public static class PlaceDto {

		@Schema(description = "장소 아이디")
		private Long id;

		@Schema(description = "장소 이름")
		private String name;

		@Schema(description = "주소")
		private String address;

		@Schema(description = "장소 지역 1단계")
		private String region1Depth;

		@Schema(description = "장소 지역 2단계")
		private String region2Depth;

		@Schema(description = "장소 지역 3단계")
		private String region3Depth;

		public static PlaceDto from(Place place) {
			return new PlaceDto(
				place.getId(),
				place.getName(),
				place.getAddress(),
				place.getRegion1depth(),
				place.getRegion2depth(),
				place.getRegion3depth()
			);
		}
	}

	public static DateSchedResponse from(DateSchedule dateSchedule) {
		Route route = dateSchedule.getRoute();
		List<RoutePlace> routePlaces = route.getRoutePlaces();
		if (routePlaces.isEmpty()) {
			throw new BusinessException(PlaceErrorCode.NOT_FOUND);
		}

		Place place = routePlaces.get(0).getPlace();
		List<Photo> photos = place.getPhotos();

		boolean isRouteIncomplete = routePlaces.stream()
			.anyMatch(routePlace -> routePlace.getPlace().getStatus() == PlaceStatus.WRITE);

		return DateSchedResponse.builder()
			.id(dateSchedule.getId())
			.name(dateSchedule.getName())
			.routeType(route.getRouteType())
			.status(isRouteIncomplete ? RouteStatus.WRITE : RouteStatus.ACTIVE)
			.places(routePlaces.stream()
				.map(rp -> PlaceDto.from(rp.getPlace()))
				.toList())
			.thumbnail(photos.isEmpty() ? null : photos.get(0).getImgUrl())
			.build();
	}
}
