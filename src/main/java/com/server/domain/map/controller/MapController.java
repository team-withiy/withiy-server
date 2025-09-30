package com.server.domain.map.controller;

import com.server.domain.map.dto.AddressDto;
import com.server.domain.map.dto.MapPlaceDto;
import com.server.domain.map.dto.RegionDto;
import com.server.domain.map.dto.request.AddressToCoordRequest;
import com.server.domain.map.dto.request.CategorySearchRequest;
import com.server.domain.map.dto.request.CoordToAddressRequest;
import com.server.domain.map.dto.request.CoordToRegionRequest;
import com.server.domain.map.dto.request.KeywordSearchRequest;
import com.server.domain.map.service.MapService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.error.code.MapErrorCode;
import com.server.global.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/map")
@Tag(name = "Map", description = "지도 관련 API")
public class MapController {

	private final MapService mapService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/address-to-coord")
	@Operation(summary = "주소를 좌표로 변환", description = "주소 문자열을 위도/경도 좌표로 변환")
	public ApiResponseDto<List<AddressDto>> addressToCoord(@RequestParam String query,
		@RequestParam(required = false) String analyzeType) {

		log.info("주소->좌표 변환 요청 받음. query: {}, analyzeType: {}", query, analyzeType);

		if (query == null || query.isBlank()) {
			throw new BusinessException(MapErrorCode.INVALID_ADDRESS);
		}

		AddressToCoordRequest request =
			AddressToCoordRequest.builder().query(query).analyzeType(analyzeType).build();

		List<AddressDto> result = mapService.addressToCoord(request);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/coord-to-address")
	@Operation(summary = "좌표를 주소로 변환", description = "위도/경도 좌표를 주소로 변환")
	public ApiResponseDto<AddressDto> coordToAddress(@RequestParam String x, @RequestParam String y,
		@RequestParam(required = false) String inputCoord) {

		validateCoordinates(x, y);

		CoordToAddressRequest request =
			CoordToAddressRequest.builder().x(x).y(y).inputCoord(inputCoord).build();

		AddressDto result = mapService.coordToAddress(request);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/coord-to-region")
	@Operation(summary = "좌표를 행정구역으로 변환", description = "위도/경도 좌표를 행정구역 정보로 변환")
	public ApiResponseDto<List<RegionDto>> coordToRegion(@RequestParam String x,
		@RequestParam String y, @RequestParam(required = false) String inputCoord,
		@RequestParam(required = false) String outputCoord) {

		validateCoordinates(x, y);

		CoordToRegionRequest request = CoordToRegionRequest.builder().x(x).y(y)
			.inputCoord(inputCoord).outputCoord(outputCoord).build();

		List<RegionDto> result = mapService.coordToRegion(request);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}

	// @ResponseStatus(HttpStatus.OK)
	// @GetMapping("/convert-coord")
	// @Operation(summary = "좌표계 변환",
	// description = "좌표계 간 변환 (WGS84, WCONGNAMUL, CONGNAMUL, WTM, TM 등)")
	// public ApiResponseDto<CoordinateDto> convertCoord(@RequestParam String x,
	// @RequestParam String y, @RequestParam String inputCoord,
	// @RequestParam String outputCoord) {

	// validateCoordinates(x, y);

	// ConvertCoordRequest request = ConvertCoordRequest.builder().x(x).y(y).inputCoord(inputCoord)
	// .outputCoord(outputCoord).build();

	// CoordinateDto result = mapService.convertCoord(request);
	// return ApiResponseDto.success(HttpStatus.OK.value(), result);
	// }

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/search/keyword")
	@Operation(summary = "키워드로 장소 검색", description = "키워드를 기반으로 장소 검색")
	public ApiResponseDto<List<MapPlaceDto>> searchByKeyword(@RequestParam String query,
		@RequestParam(required = false) String categoryGroupCode,
		@RequestParam(required = false) String x, @RequestParam(required = false) String y,
		@RequestParam(required = false) Integer radius,
		@RequestParam(required = false) String rect,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false) Integer size,
		@RequestParam(required = false) String sort) {

		log.info("키워드 검색 요청 컨트롤러에 도달. 원본 쿼리: [{}], 바이트 표현: {}", query, byteRepresentation(query));

		if (query == null || query.isBlank()) {
			throw new BusinessException(MapErrorCode.INVALID_PARAMETER);
		}

		// 추가 인코딩 처리 시도
		String decodedQuery = decodeQueryParam(query);
		log.info("Received keyword search request with query: {}, decoded to: {}, 바이트 표현: {}",
			query, decodedQuery, byteRepresentation(decodedQuery));

		validateCoordinatesIfPresent(x, y);

		KeywordSearchRequest request = KeywordSearchRequest.builder().query(decodedQuery)
			.categoryGroupCode(categoryGroupCode).x(x).y(y).radius(radius).rect(rect).page(page)
			.size(size).sort(sort).build();

		List<MapPlaceDto> result = mapService.searchByKeyword(request);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/search/category")
	@Operation(summary = "카테고리로 장소 검색", description = "카테고리 코드를 기반으로 장소 검색")
	public ApiResponseDto<List<MapPlaceDto>> searchByCategory(
		@RequestParam String categoryGroupCode,
		@RequestParam(required = false) String x, @RequestParam(required = false) String y,
		@RequestParam(required = false) Integer radius,
		@RequestParam(required = false) String rect,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false) Integer size,
		@RequestParam(required = false) String sort) {

		if (categoryGroupCode == null || categoryGroupCode.isBlank()) {
			throw new BusinessException(MapErrorCode.INVALID_PARAMETER);
		}

		validateCoordinatesIfPresent(x, y);

		CategorySearchRequest request =
			CategorySearchRequest.builder().categoryGroupCode(categoryGroupCode).x(x).y(y)
				.radius(radius).rect(rect).page(page).size(size).sort(sort).build();

		List<MapPlaceDto> result = mapService.searchByCategory(request);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}

	private void validateCoordinates(String x, String y) {
		if (x == null || y == null || x.isBlank() || y.isBlank()) {
			throw new BusinessException(MapErrorCode.INVALID_COORDINATES);
		}

		try {
			Double.parseDouble(x);
			Double.parseDouble(y);
		} catch (NumberFormatException e) {
			throw new BusinessException(MapErrorCode.INVALID_COORDINATES);
		}
	}

	private void validateCoordinatesIfPresent(String x, String y) {
		if ((x != null && !x.isBlank()) || (y != null && !y.isBlank())) {
			// 둘 중 하나만 있는 경우
			if ((x == null || x.isBlank()) || (y == null || y.isBlank())) {
				throw new BusinessException(MapErrorCode.INVALID_COORDINATES);
			}

			try {
				Double.parseDouble(x);
				Double.parseDouble(y);
			} catch (NumberFormatException e) {
				throw new BusinessException(MapErrorCode.INVALID_COORDINATES);
			}
		}
	}

	/**
	 * URL 인코딩된 쿼리 파라미터를 디코딩합니다. 다양한 방식으로 인코딩된 문자열을 처리합니다.
	 */
	private String decodeQueryParam(String query) {
		if (query == null) {
			return "";
		}

		log.info("쿼리 디코딩 시작: {}", query);

		try {
			// 기본 URL 디코딩 시도
			String decoded = URLDecoder.decode(query, StandardCharsets.UTF_8.name());
			log.debug("1차 디코딩 결과: {}", decoded);

			// 여전히 '%' 문자가 있다면 다시 한번 디코딩 (이중 인코딩된 경우)
			if (decoded.contains("%")) {
				String doubleDecoded = URLDecoder.decode(decoded, StandardCharsets.UTF_8.name());
				log.debug("2차 디코딩 결과: {}", doubleDecoded);
				decoded = doubleDecoded;
			}

			// 0x 형태의 인코딩 처리 (예: 0xeb0x8f0x84...)
			if (decoded.contains("0x")) {
				log.info("0x 형태의 인코딩 발견: {}", decoded);
				StringBuilder sb = new StringBuilder();
				int i = 0;

				while (i < decoded.length()) {
					if (i + 3 <= decoded.length() && decoded.substring(i, i + 2).equals("0x")) {
						// 충분한 길이가 있고 0x로 시작하는 경우
						try {
							int hex = Integer.parseInt(decoded.substring(i + 2, i + 4), 16);
							char decodedChar = (char) hex;
							sb.append(decodedChar);
							log.debug("0x 형식 변환: 0x{} -> {}", decoded.substring(i + 2, i + 4),
								decodedChar);
							i += 4; // 0x와 2자리 16진수 건너뜀
						} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
							// 16진수 파싱 실패 시 문자 그대로 추가
							log.debug("0x 형식 변환 실패, 원본 문자 사용: {}", decoded.charAt(i));
							sb.append(decoded.charAt(i));
							i++;
						}
					} else {
						// 일반 문자인 경우 그대로 추가
						sb.append(decoded.charAt(i));
						i++;
					}
				}

				String resultDecoded = sb.toString();
				log.info("0x 형태 디코딩 최종 결과: {}", resultDecoded);
				return resultDecoded;
			}

			log.info("디코딩 최종 결과: {}", decoded);
			return decoded;
		} catch (UnsupportedEncodingException e) {
			log.error("Error decoding query parameter: {}", e.getMessage());
			return query; // 디코딩 실패 시 원래 값 반환
		}
	}

	/**
	 * 문자열의 바이트 표현을 반환 (디버깅용)
	 */
	private String byteRepresentation(String str) {
		if (str == null) {
			return "null";
		}

		StringBuilder result = new StringBuilder();
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

		for (byte b : bytes) {
			result.append(String.format("0x%02X ", b));
		}

		return result.toString();
	}
}
