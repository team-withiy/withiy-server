package com.server.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrawlingEventDto {

	@JsonProperty("ranking")
	private int ranking;

	@JsonProperty("genre")
	private String genre;

	@JsonProperty("title")
	private String title;

	@JsonProperty("place")
	private String place;

	@JsonProperty("date")
	private String date;

	@JsonProperty("image")
	private String image;
}