package com.server.domain.map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySearchRequest {
    private String categoryGroupCode; // 카테고리 그룹 코드
    private String x; // 중심 경도(longitude)
    private String y; // 중심 위도(latitude)
    private Integer radius; // 검색 반경(미터)
    private String rect; // 검색 영역 좌표(좌X,좌Y,우X,우Y)
    private Integer page; // 결과 페이지 번호
    private Integer size; // 한 페이지 결과 수
    private String sort; // 정렬 방식(distance)
}
