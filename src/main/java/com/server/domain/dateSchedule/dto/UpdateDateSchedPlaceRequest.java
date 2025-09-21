package com.server.domain.dateSchedule.dto;

import com.server.domain.category.entity.Category;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDateSchedPlaceRequest {
    private List<PlacePhotoDto> privatePhotoUrl;
    private List<PlacePhotoDto> publicPhotoUrl;
    private Category category;
    private Long score;
    private String review;
    private String hashTag;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlacePhotoDto {
        private int order;
        private String url;
    }
}

