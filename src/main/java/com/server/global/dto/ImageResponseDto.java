package com.server.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이미지 업로드에 대한 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponseDto {

    /**
     * 이미지 URL (CDN URL)
     */
    private String imageUrl;

    /**
     * 이미지를 사용하는 엔티티 타입
     */
    private String entityType;

    /**
     * 이미지와 관련된 엔티티 ID (없는 경우 null)
     */
    private Long entityId;
}
