package com.server.domain.event.dto;

import com.server.domain.event.entity.Event;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	public static EventDto from(Event event) {
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
