package com.server.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
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