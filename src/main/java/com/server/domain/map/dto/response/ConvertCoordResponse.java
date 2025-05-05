package com.server.domain.map.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.domain.map.dto.CoordinateDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertCoordResponse {
    private Meta meta;
    private List<Document> documents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        @JsonProperty("total_count")
        private Integer totalCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private Double x; // X 좌표, 경위도인 경우 경도(longitude)
        private Double y; // Y 좌표, 경위도인 경우 위도(latitude)
    }

    public static CoordinateDto toCoordinateDto(Document document) {
        return CoordinateDto.builder().longitude(String.valueOf(document.getX()))
                .latitude(String.valueOf(document.getY())).build();
    }
}
