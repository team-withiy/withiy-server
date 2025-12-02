package com.server.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@ConditionalOnProperty(
	name = "spring.data.redis.enabled",
	havingValue = "true",
	matchIfMissing = false
)
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
		SingleServerConfig serverConfig = config.useSingleServer();
		String address = String.format("redis://%s:%d", redisHost, redisPort);

		serverConfig
			.setAddress(address)
			.setPassword(redisPassword)
			.setConnectionPoolSize(50)
			.setConnectionMinimumIdleSize(10)
			.setIdleConnectionTimeout(10000)
			.setConnectTimeout(10000)
			.setTimeout(3000)
			.setRetryAttempts(3)
			.setRetryInterval(1500);

		return Redisson.create(config);
	}
}