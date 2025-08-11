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
public class CreatePlaceByUserDto {

	private List<PhotoDto> photos;
	private String placeName;
	private String address;
	private String region1depth;
	private String region2depth;
	private String region3depth;
	private String latitude;
	private String longitude;
	private String category;
	private String review;
	private Long score;

}
