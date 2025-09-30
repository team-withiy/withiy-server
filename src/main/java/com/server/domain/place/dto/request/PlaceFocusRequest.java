package com.server.domain.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceFocusRequest {

	@Schema(description = "위도", example = "37.5665", required = true)
	private double latitude;
	@Schema(description = "경도", example = "126.9780", required = true)
	private double longitude;
	@Schema(description = "반경(km)", example = "1000", required = true)
	private double radius;
}
