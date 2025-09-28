package com.server.domain.map.dto;

import com.server.domain.place.dto.LocationDto;
import com.server.domain.place.dto.PlaceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapPlaceDto {

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

	public PlaceDto toPlaceDto() {
		return PlaceDto.builder()
			.id(null) // MapPlaceDto에는 고유 ID가 없으므로 null로 설정
			.name(this.placeName)
			.address(this.roadAddressName != null && !this.roadAddressName.isEmpty()
				? this.roadAddressName : this.addressName)
			.location(toLocationDto())
			.category(null) // 카테고리 정보는 별도로 매핑 필요
			.build();
	}

	public LocationDto toLocationDto() {
		// 주소 선택 (addressName 우선, 없으면 roadAddressName)
		String baseAddress = (this.addressName != null && !this.addressName.isEmpty())
			? this.addressName
			: this.roadAddressName;

		if (baseAddress == null || baseAddress.isEmpty()) {
			return null; // 주소 정보가 없으면 LocationDto 생성 불가
		}

		// 공백으로 분리
		String[] parts = baseAddress.split(" ");
		String region1 = parts.length > 0 ? parts[0] : null;
		String region2 = parts.length > 1 ? parts[1] : null;
		String region3 = parts.length > 2 ? parts[2] : null;

		return LocationDto.builder()
			.latitude(this.coordinates != null ? this.coordinates.getLatitude() : null)
			.longitude(this.coordinates != null ? this.coordinates.getLongitude() : null)
			.region1depth(region1)
			.region2depth(region2)
			.region3depth(region3)
			.build();
	}
}
