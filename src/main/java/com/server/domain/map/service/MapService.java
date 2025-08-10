package com.server.domain.map.service;

import com.server.domain.map.dto.AddressDto;
import com.server.domain.map.dto.CoordinateDto;
import com.server.domain.map.dto.PlaceDto;
import com.server.domain.map.dto.RegionDto;
import com.server.domain.map.dto.request.AddressToCoordRequest;
import com.server.domain.map.dto.request.CategorySearchRequest;
import com.server.domain.map.dto.request.ConvertCoordRequest;
import com.server.domain.map.dto.request.CoordToAddressRequest;
import com.server.domain.map.dto.request.CoordToRegionRequest;
import com.server.domain.map.dto.request.KeywordSearchRequest;
import com.server.domain.map.dto.response.AddressToCoordResponse;
import com.server.domain.map.dto.response.CategorySearchResponse;
import com.server.domain.map.dto.response.ConvertCoordResponse;
import com.server.domain.map.dto.response.CoordToAddressResponse;
import com.server.domain.map.dto.response.CoordToRegionResponse;
import com.server.domain.map.dto.response.KeywordSearchResponse;
import com.server.global.error.code.MapErrorCode;
import com.server.global.error.exception.BusinessException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {

	private static final String ADDRESS_TO_COORD_PATH = "/v2/local/search/address.json";
	private static final String COORD_TO_ADDRESS_PATH = "/v2/local/geo/coord2address.json";
	private static final String COORD_TO_REGION_PATH = "/v2/local/geo/coord2regioncode.json";
	private static final String CONVERT_COORD_PATH = "/v2/local/geo/transcoord.json";
	private static final String KEYWORD_SEARCH_PATH = "/v2/local/search/keyword.json";
	private static final String CATEGORY_SEARCH_PATH = "/v2/local/search/category.json";
	private final WebClient kakaoMapApiClient;

	/**
	 * 주소를 좌표로 변환
	 */
	@Cacheable(value = "addressToCoord", key = "#request.query")
	public List<AddressDto> addressToCoord(AddressToCoordRequest request) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(ADDRESS_TO_COORD_PATH)
			.queryParam("query", request.getQuery());

		if (request.getAnalyzeType() != null) {
			uriBuilder.queryParam("analyze_type", request.getAnalyzeType());
		}

		String uri = uriBuilder.build().toUriString();

		try {
			AddressToCoordResponse response = kakaoMapApiClient.get().uri(uri).retrieve()
				.bodyToMono(AddressToCoordResponse.class).block();

			if (response == null || response.getDocuments() == null
				|| response.getDocuments().isEmpty()) {
				return List.of();
			}

			return response.getDocuments().stream().map(AddressToCoordResponse::toAddressDto)
				.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Failed to convert address to coordinates: {}", e.getMessage());
			throw new BusinessException(MapErrorCode.API_ERROR);
		}
	}

	/**
	 * 좌표를 주소로 변환
	 */
	@Cacheable(value = "coordToAddress", key = "#request.x + '-' + #request.y")
	public AddressDto coordToAddress(CoordToAddressRequest request) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(COORD_TO_ADDRESS_PATH)
			.queryParam("x", request.getX()).queryParam("y", request.getY());

		if (request.getInputCoord() != null) {
			uriBuilder.queryParam("input_coord", request.getInputCoord());
		}

		String uri = uriBuilder.build().toUriString();

		try {
			CoordToAddressResponse response = kakaoMapApiClient.get().uri(uri).retrieve()
				.bodyToMono(CoordToAddressResponse.class).block();

			if (response == null || response.getDocuments() == null
				|| response.getDocuments().isEmpty()) {
				return null;
			}

			return CoordToAddressResponse.toAddressDto(response.getDocuments().get(0),
				request.getX(), request.getY());
		} catch (Exception e) {
			log.error("Failed to convert coordinates to address: {}", e.getMessage());
			throw new BusinessException(MapErrorCode.API_ERROR);
		}
	}

	/**
	 * 좌표를 행정구역 정보로 변환
	 */
	@Cacheable(value = "coordToRegion", key = "#request.x + '-' + #request.y")
	public List<RegionDto> coordToRegion(CoordToRegionRequest request) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(COORD_TO_REGION_PATH)
			.queryParam("x", request.getX()).queryParam("y", request.getY());

		if (request.getInputCoord() != null) {
			uriBuilder.queryParam("input_coord", request.getInputCoord());
		}

		if (request.getOutputCoord() != null) {
			uriBuilder.queryParam("output_coord", request.getOutputCoord());
		}

		String uri = uriBuilder.build().toUriString();

		try {
			CoordToRegionResponse response = kakaoMapApiClient.get().uri(uri).retrieve()
				.bodyToMono(CoordToRegionResponse.class).block();

			if (response == null || response.getDocuments() == null
				|| response.getDocuments().isEmpty()) {
				return List.of();
			}

			return response.getDocuments().stream().map(CoordToRegionResponse::toRegionDto)
				.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Failed to convert coordinates to region: {}", e.getMessage());
			throw new BusinessException(MapErrorCode.API_ERROR);
		}
	}

	/**
	 * 좌표계 변환
	 */
	@Cacheable(value = "convertCoord",
		key = "#request.x + '-' + #request.y + '-' + #request.inputCoord + '-' + #request.outputCoord")
	public CoordinateDto convertCoord(ConvertCoordRequest request) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(CONVERT_COORD_PATH)
			.queryParam("x", request.getX()).queryParam("y", request.getY())
			.queryParam("input_coord", request.getInputCoord())
			.queryParam("output_coord", request.getOutputCoord());

		String uri = uriBuilder.build().toUriString();

		try {
			ConvertCoordResponse response = kakaoMapApiClient.get().uri(uri).retrieve()
				.bodyToMono(ConvertCoordResponse.class).block();

			if (response == null || response.getDocuments() == null
				|| response.getDocuments().isEmpty()) {
				return null;
			}

			return ConvertCoordResponse.toCoordinateDto(response.getDocuments().get(0));
		} catch (Exception e) {
			log.error("Failed to convert coordinate system: {}", e.getMessage());
			throw new BusinessException(MapErrorCode.API_ERROR);
		}
	}

	/**
	 * 키워드로 장소 검색
	 */
	@Cacheable(value = "keywordSearch",
		key = "#request.query + '-' + #request.categoryGroupCode + '-' + #request.x + '-' + #request.y + '-' + #request.radius")
	public List<PlaceDto> searchByKeyword(KeywordSearchRequest request) {
		try {
			// URI 템플릿 방식으로 변경하여 자동 인코딩 방지
			String uri = KEYWORD_SEARCH_PATH;

			log.info("키워드 검색 요청 파라미터 - 쿼리: [{}], 카테고리: [{}], 좌표: [{},{}], 반경: [{}]",
				request.getQuery(), request.getCategoryGroupCode(), request.getX(), request.getY(),
				request.getRadius());

			KeywordSearchResponse response = kakaoMapApiClient.get()
				.uri(uriBuilder -> {
					uriBuilder.path(uri).queryParam("query", request.getQuery());

					if (request.getCategoryGroupCode() != null) {
						uriBuilder.queryParam("category_group_code",
							request.getCategoryGroupCode());
					}

					if (request.getX() != null && request.getY() != null) {
						uriBuilder.queryParam("x", request.getX())
							.queryParam("y", request.getY());

						if (request.getRadius() != null) {
							uriBuilder.queryParam("radius", request.getRadius());
						}
					}

					if (request.getRect() != null) {
						uriBuilder.queryParam("rect", request.getRect());
					}

					if (request.getPage() != null) {
						uriBuilder.queryParam("page", request.getPage());
					}

					if (request.getSize() != null) {
						uriBuilder.queryParam("size", request.getSize());
					}

					if (request.getSort() != null) {
						uriBuilder.queryParam("sort", request.getSort());
					}

					return uriBuilder.build();
				})
				.retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
					clientResponse -> {
						log.error("카카오 API 오류 응답: {}", clientResponse.statusCode());
						return clientResponse.bodyToMono(String.class)
							.flatMap(errorBody -> {
								log.error("오류 응답 본문: {}", errorBody);
								return Mono.error(new BusinessException(MapErrorCode.API_ERROR));
							});
					})
				.bodyToMono(KeywordSearchResponse.class)
				.block();

			if (response == null || response.getDocuments() == null) {
				log.info("키워드 검색 결과 없음 (빈 응답). 쿼리: [{}]", request.getQuery());
				return List.of();
			}

			log.info("키워드 검색 결과: {} 개의 항목 찾음. 쿼리: [{}]",
				response.getDocuments().size(), request.getQuery());

			return response.getDocuments().stream()
				.map(KeywordSearchResponse::toPlaceDto)
				.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("키워드 검색 중 오류 발생: {}", e.getMessage(), e);
			throw new BusinessException(MapErrorCode.API_ERROR);
		}
	}

	/**
	 * 카테고리로 장소 검색
	 */
	@Cacheable(value = "categorySearch",
		key = "#request.categoryGroupCode + '-' + #request.x + '-' + #request.y + '-' + #request.radius")
	public List<PlaceDto> searchByCategory(CategorySearchRequest request) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(CATEGORY_SEARCH_PATH)
			.queryParam("category_group_code", request.getCategoryGroupCode());

		if (request.getX() != null && request.getY() != null) {
			uriBuilder.queryParam("x", request.getX()).queryParam("y", request.getY());

			if (request.getRadius() != null) {
				uriBuilder.queryParam("radius", request.getRadius());
			}
		}

		if (request.getRect() != null) {
			uriBuilder.queryParam("rect", request.getRect());
		}

		if (request.getPage() != null) {
			uriBuilder.queryParam("page", request.getPage());
		}

		if (request.getSize() != null) {
			uriBuilder.queryParam("size", request.getSize());
		}

		if (request.getSort() != null) {
			uriBuilder.queryParam("sort", request.getSort());
		}

		String uri = uriBuilder.build().toUriString();

		try {
			CategorySearchResponse response = kakaoMapApiClient.get().uri(uri).retrieve()
				.bodyToMono(CategorySearchResponse.class).block();

			if (response == null || response.getDocuments() == null
				|| response.getDocuments().isEmpty()) {
				return List.of();
			}

			return response.getDocuments().stream().map(CategorySearchResponse::toPlaceDto)
				.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Failed to search by category: {}", e.getMessage());
			throw new BusinessException(MapErrorCode.API_ERROR);
		}
	}
}
