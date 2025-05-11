package com.server.domain.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.event.dto.CrawlingEventDto;
import com.server.domain.event.dto.CrawlingEventDtoList;
import com.server.domain.event.dto.EventDto;
import com.server.domain.event.service.EventService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.etc.HmacUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService eventService;

    private EventController eventController;

    private String SECRET_KEY;
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private List<EventDto> sampleEvents;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SECRET_KEY = "test_secret_key_7a8b9c0d1e2f3g4h5i6j7k8l9m0n1o2p3q4r5s6t7u8v9w0x1y2z3a4b5c6d";


        eventController = new EventController(eventService, objectMapper);


        // @Value("${hmac.secret-key}")를 수동으로 주입
        injectSecretKey(eventController, SECRET_KEY);

        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();



        sampleEvents = new ArrayList<>();
        sampleEvents.add(EventDto.builder().ranking(1).genre("콘서트").title("Sample Concert")
                .place("Seoul Arena").startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30)).thumbnail("http://example.com/image.jpg")
                .build());

        sampleEvents.add(EventDto.builder().ranking(2).genre("뮤지컬").title("Sample Musical")
                .place("LG Arts Center").startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(60)).thumbnail("http://example.com/musical.jpg")
                .build());
    }

    @Test
    @DisplayName("이벤트 정보 가져오기 성공 테스트")
    void getEventsSuccessTest() {
        // Given
        when(eventService.getEvents()).thenReturn(sampleEvents);

        // When
        ApiResponseDto<List<EventDto>> response = eventController.getEvents();

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(sampleEvents.size(), response.getData().size());
        assertEquals(sampleEvents.get(0).getTitle(), response.getData().get(0).getTitle());
        assertEquals(sampleEvents.get(1).getGenre(), response.getData().get(1).getGenre());
    }

    @Test
    @DisplayName("외부 크롤러에서 이벤트 저장하기 성공 테스트")
    void saveEventsFromExternalSuccessTest() throws Exception {
        // Given
        CrawlingEventDtoList crawlingEventDtoList = new CrawlingEventDtoList();
        List<CrawlingEventDto> eventList = new ArrayList<>();

        // 첫 번째 이벤트
        CrawlingEventDto dto1 = new CrawlingEventDto();
        dto1.setRanking(1);
        dto1.setGenre("콘서트");
        dto1.setTitle("Sample Concert");
        dto1.setPlace("Seoul Arena");
        dto1.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.M.d")) + " ~ " +
                LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy.M.d")));
        dto1.setImage("http://example.com/image.jpg");

        // 두 번째 이벤트
        CrawlingEventDto dto2 = new CrawlingEventDto();
        dto2.setRanking(2);
        dto2.setGenre("뮤지컬");
        dto2.setTitle("Sample Musical");
        dto2.setPlace("LG Arts Center");
        dto2.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.M.d")) + " ~ " +
                LocalDate.now().plusDays(60).format(DateTimeFormatter.ofPattern("yyyy.M.d")));
        dto2.setImage("http://example.com/musical.jpg");

        eventList.add(dto1);
        eventList.add(dto2);
        crawlingEventDtoList.setEvents(eventList);

        // 요청 본문 JSON 문자열로 직렬화
        String requestBody = objectMapper.writeValueAsString(crawlingEventDtoList);

        // HMAC 서명 생성
        String signature = HmacUtil.hmacSha256(requestBody, SECRET_KEY);

        // Mock HttpServletRequest 설정
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("X-Signature")).thenReturn(signature);

        // EventService의 saveEvents 메서드 모킹
        when(eventService.saveEvents(any(CrawlingEventDtoList.class))).thenReturn(sampleEvents);

        // When
        ApiResponseDto<List<EventDto>> response = eventController.saveEventsFromExternal(mockRequest, crawlingEventDtoList);

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(sampleEvents.size(), response.getData().size());
        assertEquals(sampleEvents.get(0).getTitle(), response.getData().get(0).getTitle());
        assertEquals(sampleEvents.get(1).getTitle(), response.getData().get(1).getTitle());

        // EventService의 saveEvents 메서드가 호출되었는지 확인
        verify(eventService).saveEvents(any(CrawlingEventDtoList.class));

        // 헤더에서 시그니처를 가져왔는지 확인
        verify(mockRequest).getHeader("X-Signature");
    }


    @Test
    @DisplayName("외부 크롤러에서 이벤트 저장 시 HMAC 검증 실패 테스트")
    void saveEventsFromExternalHmacFailureTest() throws Exception {
        // Given
        CrawlingEventDtoList crawlingEventDtoList = new CrawlingEventDtoList();
        List<CrawlingEventDto> eventList = new ArrayList<>();

        CrawlingEventDto dto = new CrawlingEventDto();
        dto.setRanking(1);
        dto.setGenre("콘서트");
        dto.setTitle("Sample Concert");
        dto.setPlace("Seoul Arena");
        dto.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.M.d")) + " ~ " +
                LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy.M.d")));
        dto.setImage("http://example.com/image.jpg");

        eventList.add(dto);
        crawlingEventDtoList.setEvents(eventList);

        // 요청 본문 JSON 문자열로 직렬화
        String requestBody = objectMapper.writeValueAsString(crawlingEventDtoList);

        // 잘못된 서명 설정
        String invalidSignature = "invalid_signature";

        // Mock HttpServletRequest 설정
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("X-Signature")).thenReturn(invalidSignature);
        when(mockRequest.getRequestURI()).thenReturn("/api/events/external");

        // When & Then
        assertThrows(Exception.class, () -> {
            eventController.saveEventsFromExternal(mockRequest, crawlingEventDtoList);
        });

        // EventService의 saveEvents 메서드가 호출되지 않았는지 확인
        verify(eventService, never()).saveEvents(any(CrawlingEventDtoList.class));

    }


    @Test
    @DisplayName("이벤트 API 엔드포인트 GET 접근 테스트")
    void getEventsEndpointTest() throws Exception {
        // Given
        when(eventService.getEvents()).thenReturn(sampleEvents);

        // When & Then
        mockMvc.perform(get("/api/events")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("이벤트 API 엔드포인트 POST 접근 테스트")
    void saveEventsEndpointTest() throws Exception {
        // Given
        CrawlingEventDtoList crawlingEventDtoList = new CrawlingEventDtoList();
        CrawlingEventDto event1 = new CrawlingEventDto();
        event1.setRanking(1);
        event1.setGenre("콘서트");
        event1.setTitle("Sample Concert");
        event1.setPlace("Seoul Arena");
        event1.setDate("2025.05.11 ~ 2025.06.11");
        event1.setImage("http://example.com/image.jpg");

        CrawlingEventDto event2 = new CrawlingEventDto();
        event2.setRanking(2);
        event2.setGenre("뮤지컬");
        event2.setTitle("Sample Musical");
        event2.setPlace("LG Arts Center");
        event2.setDate("2025.05.11 ~ 2025.07.11");
        event2.setImage("http://example.com/musical.jpg");

        List<CrawlingEventDto> eventList = Arrays.asList(event1, event2);
        crawlingEventDtoList.setEvents(eventList);

        // 요청 본문 JSON 문자열로 직렬화
        String requestBody = objectMapper.writeValueAsString(crawlingEventDtoList);

        // HMAC 서명 생성
        String signature = HmacUtil.hmacSha256(requestBody, SECRET_KEY);

        // Mock HttpServletRequest 설정
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("X-Signature")).thenReturn(signature);

        // EventService의 saveEvents 메서드 모킹
        when(eventService.saveEvents(any(CrawlingEventDtoList.class))).thenReturn(sampleEvents);

        // When & Then
        mockMvc.perform(post("/api/events/external")
                        .contentType("application/json")
                        .content(requestBody)
                        .header("X-Signature", signature))
                .andExpect(status().isOk());  // 응답 상태가 200인지 확인
    }



    private void injectSecretKey(EventController controller, String secretKey) {
        try {
            Field field = EventController.class.getDeclaredField("SECRET_KEY");
            field.setAccessible(true);
            field.set(controller, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject secret key into controller", e);
        }
    }

}
