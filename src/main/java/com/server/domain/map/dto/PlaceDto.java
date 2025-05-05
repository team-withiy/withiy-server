package com.server.domain.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {
    private String id; // 장소 ID
    private String placeName; // 장소명
    private String categoryName; // 카테고리 이름
    private String categoryGroupCode; // 중요 카테고리 그룹 코드
    private String categoryGroupName; // 중요 카테고리 그룹명
    private String phone; // 전화번호
    private String addressName; // 지번 주소
    private String roadAddressName; // 도로명 주소
    private String placeUrl; // 장소 상세페이지 URL
    private String distance; // 중심좌표까지의 거리
    private CoordinateDto coordinates; // 좌표
}
