package com.server.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;


@TestConfiguration
@Profile("test") // 테스트 환경에서만 활성화
public class TestRedisConfig {

	@Bean(initMethod = "start", destroyMethod = "stop")
	public GenericContainer<?> redisContainer() {
		GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
			.withExposedPorts(6379)
			.withReuse(true); // 컨테이너 재사용으로 속도 향상
		return redis;
	}

	@Bean
	@Primary // 테스트용 RedissonClient를 우선 사용
	public RedissonClient redissonClient(GenericContainer<?> redisContainer) {
		String address =
			"redis://" + redisContainer.getHost() + ":" + redisContainer.getMappedPort(6379);

		Config config = new Config();
		config.useSingleServer()
			.setAddress(address);

		return Redisson.create(config);
	}
}

