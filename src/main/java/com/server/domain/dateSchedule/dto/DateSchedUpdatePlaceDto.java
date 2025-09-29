package com.server.domain.dateSchedule.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateSchedUpdatePlaceDto {

    @NotNull
    private Long placeId;

    @Valid
    private List<PlacePhotoDto> privatePhotoUrl;

    @Valid
    private List<PlacePhotoDto> publicPhotoUrl;

    @NotNull
    private Long categoryId;

    @NotNull
    @Max(value = 100)
    private Long score;

    @Size(max = 2000)
    private String review;

    @Size(max = 500)
    private String hashTag;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlacePhotoDto {

        @NotNull
        private int order;

        @NotEmpty
        private String url;
    }
}

