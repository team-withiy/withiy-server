package com.server.domain.dateSchedule.service;

import com.server.domain.album.entity.Album;
import com.server.domain.dateSchedule.dto.DateSchedCreateRequest;
import com.server.domain.dateSchedule.dto.DateSchedPlaceDto;
import com.server.domain.dateSchedule.dto.DateSchedResponse;
import com.server.domain.dateSchedule.dto.UpdateDateSchedPlaceRequest;
import com.server.domain.dateSchedule.dto.UpdateDateSchedPlaceRequest.PlacePhotoDto;
import com.server.domain.dateSchedule.entity.DateSchedule;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.route.dto.RouteStatus;
import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RoutePlace;
import com.server.domain.route.entity.RouteType;
import com.server.domain.route.service.RouteService;
import com.server.domain.user.entity.User;
import com.server.global.error.code.DateSchedErrorCode;
import com.server.global.error.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DateSchedFacade {
    private final PlaceService placeService;
    private final RouteService routeService;
    private final DateSchedService dateSchedService;

    @Transactional
    public void createDateSchedule(User user, DateSchedCreateRequest request) {
        Route route = Route.builder()
                .name(request.getName())
                .status(RouteStatus.REPORT)
                .createdBy(user)
                .routeType(request.getPlaces().size() == 1 ? RouteType.PLACE : RouteType.COURSE)
                .build();
        routeService.saveRoute(route);

        for (DateSchedPlaceDto placeDto : request.getPlaces()) {
            Place place = Place.builder()
                    .name(request.getName())
                    .region1depth(placeDto.getRegion1depth())
                    .region2depth(placeDto.getRegion2depth())
                    .region3depth(placeDto.getRegion3depth())
                    .address(placeDto.getAddress())
                    .latitude(placeDto.getLatitude())
                    .longitude(placeDto.getLongitude())
                    .score(0L)
                    .user(user)
                    .status(PlaceStatus.REPORT)
                    .build();

            RoutePlace routePlace = new RoutePlace(route, placeService.save(place));
            routeService.saveRoutePlace(routePlace);
        }

        DateSchedule dateSchedule = DateSchedule.builder()
                .name(request.getName())
                .scheduleAt(LocalDate.parse(request.getScheduleAt()))
                .route(route)
                .user(user)
                .build();

        dateSchedService.save(dateSchedule);
    }

    public List<DateSchedResponse> getDateSchedule(User user, String format, String date) {
        List<DateSchedule> dateSchedules = switch (format.toLowerCase()) {
            case "month" -> dateSchedService.findByScheduleAtYyyyMm(user, date);
            case "day" -> dateSchedService.findByScheduleAtYyyyMmDd(user, date);
            default -> throw new BusinessException(DateSchedErrorCode.INVALID_DATE_FORMAT);
        };

        return dateSchedules
                .stream()
                .map(DateSchedResponse::from)
                .toList();
    }

    public List<DateSchedResponse> updatePlaceInDateSchedule(User user, Long placeId, UpdateDateSchedPlaceRequest request) {
        Place place = placeService.getPlaceById(placeId);

        for (PlacePhotoDto dto : request.getPrivatePhotoUrl()) {
            Photo photo = Photo.builder()
                    .imgUrl(dto.getUrl())
                    .place(place)
                    .type(PhotoType.PRIVATE)
                    .build();
        }

        return null;
    }

//    public List<DateSchedDetailResponse> getDateSchedulerById(User user, Long dateSchedId) {
//        DateSchedule dateSchedule = dateSchedService.findByUserAndId(user, dateSchedId);
//
//        return dateSchedule.getRoute().getRoutePlaces()
//                .stream()
//                .map(DateSchedDetailResponse::from)
//                .toList();
//    }
}
