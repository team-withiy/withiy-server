package com.server.domain.route.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewInRouteDto {

	private Long reviewId;
	private Long placeId;
	private String placeName;
	private String contents;
	private List<ReviewPhotoDto> photos;
	private UploaderDto reviewer;
	private Long score;

}
