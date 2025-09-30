package com.server.domain.place.dto.response;

import com.server.domain.place.dto.PlaceDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceFocusResponse {

	List<PlaceDto> places;

}
