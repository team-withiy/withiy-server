package com.server.domain.dateSchedule.dto;

import com.server.domain.photo.entity.Photo;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.entity.Place;
import com.server.domain.route.entity.RoutePlace;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateSchedDetailResponse {
    @Schema(description = "일정 썸네일")
    private String thumbnail;

    @Schema(description = "일정명")
    private String name;

    @Schema(description = "")
    private String address;

    @Schema(description = "장소 지역 1단계")
    private String region1Depth;

    @Schema(description = "장소 지역 2단계")
    private String region2Depth;

    @Schema(description = "장소 지역 3단계")
    private String region3Depth;

    private PlaceStatus placeStatus;

    public static DateSchedDetailResponse from(RoutePlace routePlace) {
        Place place = routePlace.getPlace();
        List<Photo> photos = place.getPhotos();
        return new DateSchedDetailResponse(
                !photos.isEmpty() ? photos.get(0).getImgUrl() : null,
                place.getName(),
                place.getAddress(),
                place.getRegion1depth(),
                place.getRegion2depth(),
                place.getRegion3depth(),
                place.getStatus()
        );
    }
}
