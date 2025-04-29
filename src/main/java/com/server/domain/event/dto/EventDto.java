package com.server.domain.event.dto;

import com.server.domain.event.entity.Event;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private int ranking;
    private String genre;
    private String title;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private String thumbnail;

    public static EventDto from(Event event){
        return EventDto.builder()
                .ranking(event.getRanking())
                .genre(event.getGenre())
                .title(event.getTitle())
                .place(event.getPlace())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .thumbnail(event.getThumbnail())
                .build();
    }
}
