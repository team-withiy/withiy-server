package com.server.domain.dateSchedule.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.service.AlbumService;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.dateSchedule.dto.DateSchedCreateRequest;
import com.server.domain.dateSchedule.dto.DateSchedPlaceDto;
import com.server.domain.dateSchedule.dto.DateSchedResponse;
import com.server.domain.dateSchedule.dto.DateSchedUpdatePlaceDto;
import com.server.domain.dateSchedule.dto.DateSchedUpdatePlaceDto.PlacePhotoDto;
import com.server.domain.dateSchedule.dto.DateSchedUpdateRequest;
import com.server.domain.dateSchedule.entity.DateSchedule;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceReview;
import com.server.domain.place.entity.PlaceStatus;
import com.server.domain.place.service.PlaceReviewService;
import com.server.domain.place.service.PlaceService;
import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RoutePlace;
import com.server.domain.route.entity.RouteStatus;
import com.server.domain.route.entity.RouteType;
import com.server.domain.route.service.RouteService;
import com.server.domain.user.entity.Couple;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.CoupleService;
import com.server.global.error.code.AlbumErrorCode;
import com.server.global.error.code.DateSchedErrorCode;
import com.server.global.error.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DateSchedFacade {

	private final CategoryService categoryService;
	private final PlaceService placeService;
	private final RouteService routeService;
	private final AlbumService albumService;
	private final DateSchedService dateSchedService;
	private final PhotoService photoService;
	private final PlaceReviewService placeReviewService;
	private final CoupleService coupleService;

	@Transactional
	public void createDateSchedule(User user, DateSchedCreateRequest request) {
		Route route = Route.builder()
			.name(request.getName())
			.status(RouteStatus.WRITE)
			.createdBy(user)
			.routeType(request.getPlaces().size() == 1 ? RouteType.PLACE : RouteType.COURSE)
			.build();
		routeService.saveRoute(route);

		for (DateSchedPlaceDto placeDto : request.getPlaces()) {
            Long placeId = placeDto.getPlaceId();
            Place place;
            if (placeId != null) {
                 place = placeService.getPlaceById(placeId);
            } else {
                validatePlace(placeDto);

                Place newPlace = Place.builder()
                    .name(request.getName())
                    .region1depth(placeDto.getRegion1depth())
                    .region2depth(placeDto.getRegion2depth())
                    .region3depth(placeDto.getRegion3depth())
                    .address(placeDto.getAddress())
                    .latitude(Double.valueOf(placeDto.getLatitude()))
                    .longitude(Double.valueOf(placeDto.getLongitude()))
                    .score(0L)
                    .user(user)
                    .status(PlaceStatus.WRITE)
                    .build();
                place = placeService.save(newPlace);
            }

			RoutePlace routePlace = new RoutePlace(route, place);
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

	@Transactional
	public void updatePlaceInDateSchedule(User user, Long dateSchedId,
		DateSchedUpdateRequest request) {
		DateSchedule dateSchedule = dateSchedService.findByUserAndId(user, dateSchedId);
		Album album = getOrCreateAlbum(user, dateSchedule);

		for (DateSchedUpdatePlaceDto placeDto : request.getPlaces()) {
			Place place = placeService.getPlaceById(placeDto.getPlaceId());
			Category category = categoryService.findById(placeDto.getCategoryId());
			place.updateScore(place.getScore());

			Optional<PlaceReview> optionalPlaceReview = placeReviewService.findByPlaceAndUser(place,
				user);
            optionalPlaceReview.ifPresentOrElse(
                // 이미 리뷰가 있으면 갱신
                placeReview -> placeReview.update(category, placeDto.getScore(), placeDto.getReview(), placeDto.getHashTag()),
                // 리뷰가 없으면 새로 생성 후 Place에 추가
                () -> place.addReview(category, placeDto.getScore(), placeDto.getReview(), placeDto.getHashTag())
            );

            refreshPhotosAndAddToAlbum(user, album, place, placeDto);
		}

		Route route = dateSchedule.getRoute();
        long reportedCount = route.getRoutePlaces().stream()
                .filter(routePlace -> routePlace.getPlace().getStatus() == PlaceStatus.REPORTED)
                .count();

        // 모든 장소가 신고되었는지 확인
        if (reportedCount > 0 && reportedCount == route.getRoutePlaces().size()) {
            route.updateStatus(RouteStatus.REPORTED);
        }
	}

    @Transactional
    public void deleteDateSchedule(User user, Long dateSchedId) {
        dateSchedService.delete(user, dateSchedId);
    }

    private void refreshPhotosAndAddToAlbum(User user, Album album, Place place,
            DateSchedUpdatePlaceDto placeDto) {

        // 기존 사진 목록 제거
        place.getPhotos().clear();

        // 1. Private 사진 저장 및 연결
        placeDto.getPrivatePhotoUrl().forEach(dto ->
                savePhotoAndAttachToPlace(dto, place, user, PhotoType.PRIVATE));

        // 2. Public 사진 저장 및 연결
        placeDto.getPublicPhotoUrl().forEach(dto ->
                savePhotoAndAttachToPlace(dto, place, user, PhotoType.PUBLIC));

        // 3. Place에 연결된 모든 새 사진을 Album에 연결
        place.getPhotos().forEach(album::addPhoto);
    }

	private Album getOrCreateAlbum(User user, DateSchedule dateSchedule) {
		Couple couple = coupleService.getCoupleOrNull(user);
		Album album = dateSchedule.getAlbum();
		if (album != null) {
			return album;
		}

		Album toSave = Album.builder()
			.title(dateSchedule.getName())
			.scheduleAt(dateSchedule.getScheduleAt())
			.couple(couple)
			.build();

		Album savedAlbum = albumService.saveAlbumAndReturn(toSave);
		dateSchedule.updateAlbum(savedAlbum);
		return savedAlbum;
	}

	private void savePhotoAndAttachToPlace(PlacePhotoDto dto, Place place, User user,
		PhotoType type) {
		Photo photo = Photo.builder()
			.imgUrl(dto.getUrl())
			.place(place)
			.type(type)
			.photoOrder(dto.getOrder())
			.user(user)
			.build();
		photoService.save(photo);

		place.addPhoto(photo);
	}

    private void validatePlace(DateSchedPlaceDto dto) {
        boolean isMissingRequiredData = !StringUtils.hasText(dto.getName()) ||
                !StringUtils.hasText(dto.getAddress()) ||
                !StringUtils.hasText(dto.getLatitude()) ||
                !StringUtils.hasText(dto.getLongitude()) ||
                !StringUtils.hasText(dto.getRegion1depth()) ||
                !StringUtils.hasText(dto.getRegion2depth()) ||
                !StringUtils.hasText(dto.getRegion3depth());

        if (isMissingRequiredData) {
            throw new BusinessException(AlbumErrorCode.INVALID_PLACE_DATA);
        }
    }
}
