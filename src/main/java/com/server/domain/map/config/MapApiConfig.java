package com.server.domain.map.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@EnableCaching
@Slf4j
public class MapApiConfig {

	private static final String KAKAO_API_BASE_URL = "https://dapi.kakao.com";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String KAKAO_AK_PREFIX = "KakaoAK ";
	private static final String KA_HEADER = "KA";
	private static final String KA_HEADER_VALUE = "sdk/1.0.0 os/javascript origin/withiy-app";
	@Value("${kakao.map.api.key}")
	private String apiKey;

	@Bean
	public WebClient kakaoMapApiClient() {
		// 로깅으로 API 키 설정 확인 (실제 환경에서는 주의 필요)
		log.info("Kakao Map API Key is set: {}", apiKey != null && !apiKey.isBlank());

		if (apiKey == null || apiKey.isBlank()) {
			log.error(
				"Kakao Map API key is not set. Please check KAKAO_MAP_SECRET environment variable.");
		}

		// 요청/응답 로깅을 위한 필터 추가
		ExchangeFilterFunction logRequest = ExchangeFilterFunction.ofRequestProcessor(request -> {
			log.info("Request: {} {}", request.method(), request.url());
			request.headers().forEach((name, values) -> {
				if (!"Authorization".equals(name)) { // 인증 정보는 로깅 제외
					values.forEach(value -> log.info("{}={}", name, value));
				} else {
					log.info("{}=<masked>", name);
				}
			});
			return Mono.just(request);
		});

		ExchangeFilterFunction logResponse =
			ExchangeFilterFunction.ofResponseProcessor(response -> {
				log.info("Response status: {}", response.statusCode());
				return Mono.just(response);
			});

		return WebClient.builder()
			.baseUrl(KAKAO_API_BASE_URL)
			.defaultHeader(AUTHORIZATION_HEADER, KAKAO_AK_PREFIX + apiKey)
			.defaultHeader(KA_HEADER, KA_HEADER_VALUE)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
			.filter(logRequest)
			.filter(logResponse)
			.build();
	}

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("addressToCoord",
			"coordToAddress", "coordToRegion", "keywordSearch", "categorySearch");

		Caffeine<Object, Object> caffeine =
			Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS) // 1시간 후 캐시 만료
				.maximumSize(1000); // 최대 1000개 항목 캐싱

		cacheManager.setCaffeine(caffeine);
		return cacheManager;
	}
}
