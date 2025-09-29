package com.server.domain.route.dto;

public enum RouteStatus {
    WRITE,
	ACTIVE,      // 운영 중
	REPORTED,    // 신고 접수
	DELETED      // 삭제 이력
}
