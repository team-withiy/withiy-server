package com.server.domain.user.controller;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@EntityScan(basePackages = "com.server.domain")
@EnableJpaRepositories(basePackages = "com.server.domain")
public class TestJpaConfig {
    // This configuration ensures that JPA entities are properly scanned in tests
}
