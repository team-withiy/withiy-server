package com.server.domain.admin;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import com.server.domain.admin.dto.ActiveContentsResponse;
import com.server.domain.admin.service.AdminFacade;
import com.server.domain.album.entity.Album;
import com.server.domain.album.service.AlbumService;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.course.dto.CourseStatus;
import com.server.domain.course.entity.Course;
import com.server.domain.course.service.CourseService;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.term.entity.Term;
import com.server.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class AdminFacadeTest {

	@InjectMocks
	private AdminFacade adminFacade;

	@Mock
	private PlaceService placeService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private AlbumService albumService;

	@Mock
	private PhotoService photoService;

	@Mock
	private CourseService courseService;

	private User user;
	private Category category;
	private Course course;
	private Place place;
	private Album album1;

	@BeforeEach
	void setUp() {
		// Setup users
		ArrayList<Term> emptyTerms = new ArrayList<>();
		user = User.builder().nickname("User1").thumbnail("user1_thumbnail.jpg").code("USER1_CODE")
			.terms(emptyTerms) // Add empty terms list instead of null
			.build();
		user.setId(1L);

		category = new Category("맛집", "https://image.com/icon.png");
		ReflectionTestUtils.setField(category, "id", 1L);

		// Place 객체 생성 (Builder 사용)
		place = Place.builder()
			.name("피자헛 강남점")
			.region1depth("서울특별시")
			.region2depth("강남구")
			.region3depth("역삼동")
			.address("서울특별시 강남구 테헤란로 123")
			.latitude("37.501274")
			.longitude("127.039585")
			.score(12L)
			.user(user)
			.category(category)
			.status(PlaceStatus.ACTIVE)
			.build();

		ReflectionTestUtils.setField(place, "id", 101L);

		album1 = new Album("피자헛 강남점", user);
		ReflectionTestUtils.setField(album1, "id", 201L);

		course = Course.builder()
			.name("피자 투어")
			.status(CourseStatus.ACTIVE)
			.createdBy(user)
			.build();
		ReflectionTestUtils.setField(course, "id", 301L);

	}

	@Test
	@DisplayName("getActiveContents: 정상 조회 시 place, course 목록이 포함된 응답을 반환한다")
	void getActiveContents_success() {
		String categoryName = "맛집";
		String keyword = "피자";

		// Mock 설정
		when(categoryService.getCategoryByName(categoryName)).thenReturn(category);
		when(placeService.getActivePlacesByCategoryAndKeyword(category, keyword)).thenReturn(
			List.of(place));
		when(placeService.getBookmarkCount(place)).thenReturn(10L);
		when(albumService.getAlbumByPlace(place)).thenReturn(album1);
		when(photoService.getPhotoUrls(album1)).thenReturn(List.of("https://image.com/photo1.jpg"));
		when(courseService.getActiveCoursesByKeyword(keyword)).thenReturn(List.of(course));
		when(courseService.getBookmarkCount(course)).thenReturn(3L);
		when(courseService.getPlacesInCourse(course)).thenReturn(List.of(place));

		// When
		ActiveContentsResponse result = adminFacade.getActiveContents(categoryName, keyword);

		// Then
		assertThat(result.getPlaces()).hasSize(1);
		assertThat(result.getCourses()).hasSize(1);

		assertThat(result.getPlaces().get(0).getPlaceId()).isEqualTo(101L);
		assertThat(result.getPlaces().get(0).getPlaceName()).isEqualTo("피자헛 강남점");
		assertThat(result.getPlaces().get(0).getBookmarkCount()).isEqualTo(10L);
		assertThat(result.getPlaces().get(0).getPhotoUrls()).containsExactly(
			"https://image.com/photo1.jpg");

		assertThat(result.getCourses().get(0).getCourseId()).isEqualTo(301L);
		assertThat(result.getCourses().get(0).getCourseName()).isEqualTo("피자 투어");
		assertThat(result.getCourses().get(0).getBookmarkCount()).isEqualTo(3L);
		assertThat(result.getCourses().get(0).getPlaceNames()).containsExactly("피자헛 강남점");
		assertThat(result.getCourses().get(0).getPhotoUrls()).containsExactly(
			"https://image.com/photo1.jpg");
	}


}