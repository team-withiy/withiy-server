package com.server.global.dto;

import lombok.Data;

@Data
public class RedisRequest {

	private String key;
	private String value;
}

