package com.server.domain.event.controller;

import com.server.domain.event.dto.EventDto;
import com.server.domain.event.service.EventService;
import com.server.global.dto.ApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private MockMvc mockMvc;
    private List<EventDto> sampleEvents;

    @BeforeEach
    void setUp() {
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

    /*@Test
    @DisplayName("이벤트 저장하기 성공 테스트")
    void saveEventsSuccessTest() throws Exception {
        // Given
        when(eventService.saveEvents()).thenReturn(sampleEvents);

        // When
        ApiResponseDto<List<EventDto>> response = eventController.saveEvents();

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(sampleEvents.size(), response.getData().size());
        assertEquals(sampleEvents.get(0).getTitle(), response.getData().get(0).getTitle());
    }*/

    @Test
    @DisplayName("이벤트 API 엔드포인트 GET 접근 테스트")
    void getEventsEndpointTest() throws Exception {
        // Given
        when(eventService.getEvents()).thenReturn(sampleEvents);

        // When & Then
        mockMvc.perform(get("/api/events")).andExpect(status().isOk());
    }

    /*@Test
    @DisplayName("이벤트 API 엔드포인트 POST 접근 테스트")
    void saveEventsEndpointTest() throws Exception {
        // Given
        when(eventService.saveEvents()).thenReturn(sampleEvents);

        // When & Then
        mockMvc.perform(post("/api/events")).andExpect(status().isOk());
    }*/
}
