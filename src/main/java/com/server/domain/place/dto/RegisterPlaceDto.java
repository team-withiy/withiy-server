package com.server.domain.place.dto;

import com.server.domain.photo.dto.PhotoDto;
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
public class RegisterPlaceDto {

	private List<PhotoDto> photos;
	private Long placeId;
	private Long score;
	private String review;
}
