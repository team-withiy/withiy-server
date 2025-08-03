package com.server.domain.admin.dto;

import com.server.domain.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ActivePlaceDto {
    @Schema(description = "장소 고유 ID", example = "1")
    private Long placeId;
    @Schema(description = "장소 이름", example = "서울숲")
    private String placeName;
    @Schema(description = "장소 주소", example = "서울특별시 성동구 서울숲길 273")
    private String placeAddress;
    @Schema(description = "관리자에 의해 생성된 장소 여부", example = "true")
    private boolean createdByAdmin;
    @Schema(description = "북마크 수", example = "100")
    private long bookmarkCount;
    @Schema(description = "좋아요 수", example = "250")
    private long likeCount;
    @Schema(description = "장소 사진 URL 목록", example = "[\"https://example.com/photo1.jpg\", \"https://example.com/photo2.jpg\"]")
    private List<String> photoUrls;
    @Schema(description = "장소 카테고리 정보")
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
