package com.server.domain.event.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public List<EventDto> saveEvents() throws Exception {
        List<EventDto> eventDtos = new ArrayList<>();
        String dirPath = "/app/withiy-crawling";  // 이벤트 랭킹 json 위치
        ObjectMapper mapper = new ObjectMapper();

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.info("디렉토리가 존재하지 않습니다: " + dirPath);
            return eventDtos;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) {
            log.info("JSON 파일이 없습니다: " + dirPath);
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
