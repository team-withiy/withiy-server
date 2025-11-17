package com.server.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Value("${spring.data.redis.password:}")
	private String redisPassword;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		String address = String.format("redis://%s:%d", redisHost, redisPort);

		config.useSingleServer()
			.setAddress(address)
			.setConnectionPoolSize(50)
			.setConnectionMinimumIdleSize(10)
			.setIdleConnectionTimeout(10000)
			.setConnectTimeout(10000)
			.setTimeout(3000)
			.setRetryAttempts(3)
			.setRetryInterval(1500);

		// 비밀번호가 있는 경우에만 설정
		if (redisPassword != null && !redisPassword.isEmpty()) {
			config.useSingleServer().setPassword(redisPassword);
		}

		return Redisson.create(config);
	}
}