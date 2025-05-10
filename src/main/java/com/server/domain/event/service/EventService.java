package com.server.domain.event.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.event.dto.CrawlingEventDto;
import com.server.domain.event.dto.CrawlingEventDtoList;
import com.server.domain.event.dto.EventDto;
import com.server.domain.event.entity.Event;
import com.server.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;

    public List<EventDto> getEvents() {
        return eventRepository.findAll()
                .stream().map(EventDto::from).toList();
    }

    @Transactional
    public List<EventDto> saveEvents(CrawlingEventDtoList crawlingEventDtoList) {
        List<CrawlingEventDto> crawlingEventDtos = crawlingEventDtoList.getEvents();
        log.info("받은 크롤링 데이터 수: {}", crawlingEventDtos.size());

        List<EventDto> result = new ArrayList<>();

        for (CrawlingEventDto crawlingDto : crawlingEventDtos) {
            // 날짜 파싱
            LocalDate[] dates = parseDateRange(crawlingDto.getDate());
            LocalDate startDate = dates[0];
            LocalDate endDate = dates[1];

            if(eventRepository.findByTitleAndStartDate(crawlingDto.getTitle(), startDate)==null) {

                // Event 엔티티 생성
                Event event = Event.builder()
                        .ranking(crawlingDto.getRanking())
                        .genre(crawlingDto.getGenre())
                        .title(crawlingDto.getTitle())
                        .place(crawlingDto.getPlace())
                        .startDate(startDate)
                        .endDate(endDate)
                        .thumbnail(crawlingDto.getImage())
                        .build();

                // 저장
                Event saved = eventRepository.save(event);

                // DTO로 변환하여 결과 리스트에 추가
                result.add(EventDto.from(saved));
            }
        }

        return result;
    }



    public LocalDate[] parseDateRange(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return new LocalDate[]{null, null};
        String[] parts = dateStr.split("~");
        String start = parts[0].trim();
        String end = parts.length > 1 ? parts[1].trim() : null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.M.d");
        LocalDate startDate = null;
        LocalDate endDate = null;

        // 시작날짜 파싱
        try {
            startDate = LocalDate.parse(start, formatter);
        } catch (Exception e) {
            // 파싱 실패 시 null
        }

        // 종료날짜 파싱
        if (end != null && !end.isBlank()) {
            // 연도 없는 종료일 처리 (예: 7.13 → 2025.7.13)
            if (end.chars().filter(ch -> ch == '.').count() == 1 && startDate != null) {
                int year = startDate.getYear();
                end = year + "." + end;
            }
            try {
                endDate = LocalDate.parse(end, formatter);
            } catch (Exception e) {
                // 파싱 실패 시 null
            }
        }

        return new LocalDate[]{startDate, endDate};
    }
}
