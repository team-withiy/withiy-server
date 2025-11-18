package com.server.global.controller;

import com.server.global.dto.RedisRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class RedisTestController {

	private final RedissonClient redissonClient;

	@PostMapping("/test")
	public ResponseEntity<?> setValue(@RequestBody RedisRequest request) {
		log.info("key: {}, value: {}", request.getKey(), request.getValue());

		RBucket<String> bucket = redissonClient.getBucket(request.getKey());
		bucket.set(request.getValue(), 60, TimeUnit.SECONDS); // TTL 60초
		return ResponseEntity.ok(
			Map.of("status", "SUCCESS", "storedValue", bucket.get())
		);
	}

	@GetMapping("/test/{key}")
	public ResponseEntity<?> getValue(@PathVariable String key) {
		RBucket<String> bucket = redissonClient.getBucket(key);
		return ResponseEntity.ok(
			Map.of("key", key, "value", bucket.get())
		);
	}
}

