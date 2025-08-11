package com.server.domain.event.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.server.domain.event.dto.EventDto;
import com.server.domain.event.entity.Event;
import com.server.domain.event.repository.EventRepository;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventServiceTest {

	@Mock
	private EventRepository eventRepository;
	private TestEventService eventService;
	private List<Event> sampleEvents;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		eventService = new TestEventService(eventRepository);

		sampleEvents = new ArrayList<>();

		Event event1 = new Event();
		event1.setId(1L);
		event1.setRanking(1);
		event1.setGenre("콘서트");
		event1.setTitle("Sample Concert");
		event1.setPlace("Seoul Arena");
		event1.setStartDate(LocalDate.of(2025, 5, 1));
		event1.setEndDate(LocalDate.of(2025, 6, 1));
		event1.setThumbnail("http://example.com/image.jpg");

		Event event2 = new Event();
		event2.setId(2L);
		event2.setRanking(2);
		event2.setGenre("뮤지컬");
		event2.setTitle("Sample Musical");
		event2.setPlace("LG Arts Center");
		event2.setStartDate(LocalDate.of(2025, 5, 15));
		event2.setEndDate(LocalDate.of(2025, 7, 15));
		event2.setThumbnail("http://example.com/musical.jpg");

		sampleEvents.add(event1);
		sampleEvents.add(event2);
	}

	@Test
	@DisplayName("이벤트 정보 가져오기 테스트")
	void getEventsTest() {
		// Given
		when(eventRepository.findAll()).thenReturn(sampleEvents);

		// When
		List<EventDto> result = eventService.getEvents();

		// Then
		assertEquals(2, result.size());
		assertEquals("Sample Concert", result.get(0).getTitle());
		assertEquals("뮤지컬", result.get(1).getGenre());
		assertEquals(LocalDate.of(2025, 5, 1), result.get(0).getStartDate());
		assertEquals(LocalDate.of(2025, 7, 15), result.get(1).getEndDate());

		verify(eventRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("날짜 파싱 - 정상 범위 테스트")
	void parseDateRangeNormalTest() {
		// Given
		String dateStr = "2025.5.1~2025.6.30";

		// When
		LocalDate[] dates = eventService.parseDateRange(dateStr);

		// Then
		assertEquals(LocalDate.of(2025, 5, 1), dates[0]);
		assertEquals(LocalDate.of(2025, 6, 30), dates[1]);
	}

	@Test
	@DisplayName("날짜 파싱 - 종료일 연도 누락 테스트")
	void parseDateRangeMissingYearTest() {
		// Given
		String dateStr = "2025.5.1~6.30";

		// When
		LocalDate[] dates = eventService.parseDateRange(dateStr);

		// Then
		assertEquals(LocalDate.of(2025, 5, 1), dates[0]);
		assertEquals(LocalDate.of(2025, 6, 30), dates[1]);
	}

	@Test
	@DisplayName("날짜 파싱 - 단일 날짜 테스트")
	void parseDateRangeSingleDateTest() {
		// Given
		String dateStr = "2025.5.1";

		// When
		LocalDate[] dates = eventService.parseDateRange(dateStr);

		// Then
		assertEquals(LocalDate.of(2025, 5, 1), dates[0]);
		assertNull(dates[1]);
	}

	@Test
	@DisplayName("날짜 파싱 - 빈 문자열 테스트")
	void parseDateRangeEmptyStringTest() {
		// Given
		String dateStr = "";

		// When
		LocalDate[] dates = eventService.parseDateRange(dateStr);

		// Then
		assertNull(dates[0]);
		assertNull(dates[1]);
	}

	@Test
	@DisplayName("날짜 파싱 - null 테스트")
	void parseDateRangeNullTest() {
		// When
		LocalDate[] dates = eventService.parseDateRange(null);

		// Then
		assertNull(dates[0]);
		assertNull(dates[1]);
	}

	@Test
	@DisplayName("날짜 파싱 - 잘못된 형식 테스트")
	void parseDateRangeInvalidFormatTest() {
		// Given
		String dateStr = "Invalid date format";

		// When
		LocalDate[] dates = eventService.parseDateRange(dateStr);

		// Then
		assertNull(dates[0]);
		assertNull(dates[1]);
	}

	@Test
	@DisplayName("이벤트 저장 - 디렉토리 없을 때 테스트")
	void saveEventsDirectoryNotExistTest() throws Exception {
		// Given - 존재하지 않는 경로로 설정
		String nonExistentPath = "/non/existent/path";
		eventService.setCustomDirPath(nonExistentPath);

		// When
		List<EventDto> result = eventService.saveEvents();

		// Then
		assertTrue(result.isEmpty());
		verify(eventRepository, never()).save(any(Event.class));
	}

	@Test
	@DisplayName("이벤트 저장 테스트")
	void saveEventsTest(@TempDir Path tempDir) throws Exception {
		// Given
		// 임시 디렉토리 경로로 설정
		String tempDirPath = tempDir.toString();
		eventService.setCustomDirPath(tempDirPath);

		// 샘플 JSON 파일 생성
		ObjectNode eventNode1 = objectMapper.createObjectNode();
		eventNode1.put("ranking", 1);
		eventNode1.put("genre", "콘서트");
		eventNode1.put("title", "Test Concert");
		eventNode1.put("place", "Test Arena");
		eventNode1.put("image", "http://example.com/test.jpg");
		eventNode1.put("date", "2025.5.1~2025.6.1");

		ObjectNode eventNode2 = objectMapper.createObjectNode();
		eventNode2.put("ranking", 2);
		eventNode2.put("genre", "뮤지컬");
		eventNode2.put("title", "Test Musical");
		eventNode2.put("place", "Test Center");
		eventNode2.put("image", "http://example.com/test2.jpg");
		eventNode2.put("date", "2025.7.1~8.31");

		ArrayNode arrayNode = objectMapper.createArrayNode();
		arrayNode.add(eventNode1);
		arrayNode.add(eventNode2);

		File testFile = tempDir.resolve("test.json").toFile();
		objectMapper.writeValue(testFile, arrayNode);

		when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
			Event savedEvent = invocation.getArgument(0);
			savedEvent.setId(1L); // DB에서 ID 할당 시뮬레이션
			return savedEvent;
		});

		// When
		List<EventDto> result = eventService.saveEvents();

		// Then
		assertEquals(2, result.size());
		assertEquals("Test Concert", result.get(0).getTitle());
		assertEquals("뮤지컬", result.get(1).getGenre());

		verify(eventRepository, times(2)).save(any(Event.class));
	}

	@Test
	@DisplayName("장르별 이벤트 조회 테스트")
	void getEventsByGenreTest() {
		// Given
		String genre = "concert";
		List<Event> concertEvents = new ArrayList<>();

		// 이벤트 샘플 추가 (콘서트 장르)
		Event event1 = new Event();
		event1.setId(1L);
		event1.setRanking(1);
		event1.setGenre("콘서트");
		event1.setTitle("Sample Concert");
		event1.setPlace("Seoul Arena");
		event1.setStartDate(LocalDate.of(2025, 5, 1));
		event1.setEndDate(LocalDate.of(2025, 6, 1));
		event1.setThumbnail("http://example.com/image.jpg");

		concertEvents.add(event1);

		// 이벤트가 조회될 때 반환될 값 설정
		when(eventRepository.findByGenre("콘서트")).thenReturn(Optional.of((concertEvents)));

		// When
		List<EventDto> result = eventService.getEventsByGenre(genre);

		// Then
		assertEquals(1, result.size());
		assertEquals("Sample Concert", result.get(0).getTitle());
		assertEquals("콘서트", result.get(0).getGenre());
		assertEquals(LocalDate.of(2025, 5, 1), result.get(0).getStartDate());
		assertEquals(LocalDate.of(2025, 6, 1), result.get(0).getEndDate());

		verify(eventRepository, times(1)).findByGenre("콘서트");
	}

	@Test
	@DisplayName("지원하지 않는 장르 테스트")
	void getEventsByInvalidGenreTest() {
		// Given
		String genre = "invalid_genre"; // 존재하지 않는 장르

		// When / Then
		assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByGenre(genre));
	}

	@Test
	@DisplayName("장르에 해당하는 이벤트가 없을 때 테스트")
	void getEventsByGenreNotFoundTest() {
		// Given
		String genre = "sports";
		when(eventRepository.findByGenre("스포츠")).thenReturn(
			Optional.of(new ArrayList<>())); // 스포츠 장르에 해당하는 이벤트가 없음

		// When
		List<EventDto> result = eventService.getEventsByGenre(genre);

		// Then
		assertTrue(result.isEmpty()); // 결과는 비어 있어야 함
		verify(eventRepository, times(1)).findByGenre("스포츠");
	}

	// We'll use a TestEventService to override the hardcoded path
	private class TestEventService extends EventService {

		private final Logger logger = LoggerFactory.getLogger(TestEventService.class);
		private String customDirPath;

		public TestEventService(EventRepository eventRepository) {
			super(eventRepository);
		}

		public void setCustomDirPath(String customDirPath) {
			this.customDirPath = customDirPath;
		}

		public List<EventDto> saveEvents() throws Exception {
			// Override the path with our custom path
			List<EventDto> eventDtos = new ArrayList<>();
			String dirPath =
				this.customDirPath != null ? this.customDirPath : "/app/withiy-crawling";
			ObjectMapper mapper = new ObjectMapper();

			File dir = new File(dirPath);
			if (!dir.exists() || !dir.isDirectory()) {
				logger.info("디렉토리가 존재하지 않습니다: " + dirPath);
				return eventDtos;
			}

			File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
			if (files == null) {
				logger.info("JSON 파일이 없습니다: " + dirPath);
				return eventDtos;
			}

			for (File file : files) {
				JsonNode root = mapper.readTree(file);
				Iterator<JsonNode> iter = root.elements();

				while (iter.hasNext()) {
					JsonNode node = iter.next();
					Event event = new Event();

					event.setRanking(node.get("ranking").asInt());
					event.setGenre(node.get("genre").asText());
					event.setTitle(node.get("title").asText());
					event.setPlace(node.get("place").asText());
					event.setThumbnail(node.get("image").asText());

					// 날짜 파싱
					String dateStr = node.get("date").asText();
					LocalDate[] dates = parseDateRange(dateStr);
					event.setStartDate(dates[0]);
					event.setEndDate(dates[1]);

					eventRepository.save(event);
					eventDtos.add(EventDto.from(event));
				}
			}
			return eventDtos;
		}
	}


}
