package com.server.domain.map.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.server.domain.map.dto.AddressDto;
import com.server.domain.map.dto.CoordinateDto;
import com.server.domain.map.dto.RegionDto;
import com.server.domain.map.dto.request.AddressToCoordRequest;
import com.server.domain.map.dto.request.CoordToAddressRequest;
import com.server.domain.map.dto.request.CoordToRegionRequest;
import com.server.domain.map.dto.response.AddressToCoordResponse;
import com.server.domain.map.dto.response.CoordToAddressResponse;
import com.server.domain.map.dto.response.CoordToRegionResponse;
import com.server.global.error.exception.BusinessException;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MapServiceTest {

    @Mock
    private WebClient webClientMock;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private ResponseSpec responseSpecMock;

    @InjectMocks
    private MapService mapService;

    @BeforeEach
    public void setup() {
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    }

    @Test
    @DisplayName("주소를 좌표로 변환 테스트 - 성공")
    void addressToCoord_Success() {
        // Given
        AddressToCoordRequest request =
                AddressToCoordRequest.builder().query("서울특별시 강남구 테헤란로 521").build();

        AddressToCoordResponse mockResponse = new AddressToCoordResponse();
        AddressToCoordResponse.Meta meta = new AddressToCoordResponse.Meta();
        meta.setTotalCount(1);
        mockResponse.setMeta(meta);

        AddressToCoordResponse.Document document = new AddressToCoordResponse.Document();
        document.setAddressName("서울특별시 강남구 테헤란로 521");
        document.setX("127.0495556");
        document.setY("37.5065100");

        AddressToCoordResponse.Document.Address address =
                new AddressToCoordResponse.Document.Address();
        address.setAddressName("서울특별시 강남구 테헤란로 521");
        address.setRegion1DepthName("서울특별시");
        address.setRegion2DepthName("강남구");
        address.setRegion3DepthName("삼성동");
        address.setMainAddressNo("158");
        address.setSubAddressNo("");
        address.setMountainYn("N");
        document.setAddress(address);

        mockResponse.setDocuments(List.of(document));

        when(responseSpecMock.bodyToMono(AddressToCoordResponse.class))
                .thenReturn(Mono.just(mockResponse));

        // When
        List<AddressDto> result = mapService.addressToCoord(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAddressName()).isEqualTo("서울특별시 강남구 테헤란로 521");
        assertThat(result.get(0).getCoordinates().getLongitude()).isEqualTo("127.0495556");
        assertThat(result.get(0).getCoordinates().getLatitude()).isEqualTo("37.5065100");
    }

    @Test
    @DisplayName("주소를 좌표로 변환 테스트 - 실패 (API 오류)")
    void addressToCoord_ApiError() {
        // Given
        AddressToCoordRequest request =
                AddressToCoordRequest.builder().query("서울특별시 강남구 테헤란로 521").build();

        when(responseSpecMock.bodyToMono(AddressToCoordResponse.class))
                .thenReturn(Mono.error(new RuntimeException("API Error")));

        // When & Then
        assertThrows(BusinessException.class, () -> mapService.addressToCoord(request));
    }

    @Test
    @DisplayName("좌표를 주소로 변환 테스트 - 성공")
    void coordToAddress_Success() {
        // Given
        CoordToAddressRequest request =
                CoordToAddressRequest.builder().x("127.0495556").y("37.5065100").build();

        CoordToAddressResponse mockResponse = new CoordToAddressResponse();
        CoordToAddressResponse.Meta meta = new CoordToAddressResponse.Meta();
        meta.setTotalCount(1);
        mockResponse.setMeta(meta);

        CoordToAddressResponse.Document document = new CoordToAddressResponse.Document();

        CoordToAddressResponse.Document.Address address =
                new CoordToAddressResponse.Document.Address();
        address.setAddressName("서울특별시 강남구 삼성동 158");
        address.setRegion1DepthName("서울특별시");
        address.setRegion2DepthName("강남구");
        address.setRegion3DepthName("삼성동");
        address.setMainAddressNo("158");
        address.setMountainYn("N");
        document.setAddress(address);

        mockResponse.setDocuments(List.of(document));

        when(responseSpecMock.bodyToMono(CoordToAddressResponse.class))
                .thenReturn(Mono.just(mockResponse));

        // When
        AddressDto result = mapService.coordToAddress(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAddressName()).isEqualTo("서울특별시 강남구 삼성동 158");
        assertThat(result.getCoordinates().getLongitude()).isEqualTo("127.0495556");
        assertThat(result.getCoordinates().getLatitude()).isEqualTo("37.5065100");
    }

    @Test
    @DisplayName("좌표를 행정구역으로 변환 테스트 - 성공")
    void coordToRegion_Success() {
        // Given
        CoordToRegionRequest request =
                CoordToRegionRequest.builder().x("127.0495556").y("37.5065100").build();

        CoordToRegionResponse mockResponse = new CoordToRegionResponse();
        CoordToRegionResponse.Meta meta = new CoordToRegionResponse.Meta();
        meta.setTotalCount(2);
        mockResponse.setMeta(meta);

        CoordToRegionResponse.Document document1 = new CoordToRegionResponse.Document();
        document1.setRegionType("H");
        document1.setAddressName("서울특별시 강남구 삼성동");
        document1.setRegion1DepthName("서울특별시");
        document1.setRegion2DepthName("강남구");
        document1.setRegion3DepthName("삼성동");
        document1.setCode("1168010500");
        document1.setX(127.0495556);
        document1.setY(37.5065100);

        CoordToRegionResponse.Document document2 = new CoordToRegionResponse.Document();
        document2.setRegionType("B");
        document2.setAddressName("서울특별시 강남구 삼성동");
        document2.setRegion1DepthName("서울특별시");
        document2.setRegion2DepthName("강남구");
        document2.setRegion3DepthName("삼성동");
        document2.setCode("1168010500");
        document2.setX(127.0495556);
        document2.setY(37.5065100);

        mockResponse.setDocuments(List.of(document1, document2));

        when(responseSpecMock.bodyToMono(CoordToRegionResponse.class))
                .thenReturn(Mono.just(mockResponse));

        // When
        List<RegionDto> result = mapService.coordToRegion(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRegionType()).isEqualTo("H");
        assertThat(result.get(0).getAddressName()).isEqualTo("서울특별시 강남구 삼성동");
        assertThat(result.get(0).getCode()).isEqualTo("1168010500");
        assertThat(result.get(1).getRegionType()).isEqualTo("B");
    }
}
