package com.server.domain.user.controller;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan("com.server")
public class TestConfig {
	// This class enables JPA entity scanning for our tests
}
