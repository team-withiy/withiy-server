package com.server.domain.report.dto;

import lombok.Getter;

@Getter
public enum ReportReasonType {
	// 📷 사진 관련
	PHOTO_INAPPROPRIATE("부적절한 사진"),         // 장소와 무관하거나 선정적/불쾌한 사진
	PHOTO_COPYRIGHT("저작권 위반 사진"),          // 본인 소유가 아닌 사진
	PHOTO_SPAM("스팸/광고성 사진"),              // 홍보, 광고 목적의 사진

	// 📍 장소 관련
	PLACE_INACCURATE("장소 정보가 부정확함"),     // 주소, 이름, 설명 등 잘못된 정보
	PLACE_DUPLICATE("중복 등록된 장소"),          // 동일 장소가 중복 등록
	PLACE_INAPPROPRIATE("부적절한 장소"),         // 불법/부적절한 장소 정보

	// 🚨 기타
	OTHER("기타 사유");                         // 사용자가 직접 입력

	private final String description;

	ReportReasonType(String description) {
		this.description = description;
	}
}
