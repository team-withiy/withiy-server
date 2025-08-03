package com.server.domain.admin.dto;

import com.server.domain.category.dto.CategoryDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ActivePlaceDto {
    private Long placeId;
    private String placeName;
    private String placeAddress;
    private boolean createdByAdmin;
    private long bookmarkCount;
    private long likeCount;
    private List<String> photoUrls;
    private CategoryDto placeCategory;

    @Builder
    public ActivePlaceDto(
            Long placeId,
            String placeName,
            String placeAddress,
            boolean createdByAdmin,
            long bookmarkCount,
            long likeCount,
            List<String> photoUrls,
            CategoryDto placeCategory) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.createdByAdmin = createdByAdmin;
        this.bookmarkCount = bookmarkCount;
        this.likeCount = likeCount;
        this.photoUrls = photoUrls;
        this.placeCategory = placeCategory;
    }
}
