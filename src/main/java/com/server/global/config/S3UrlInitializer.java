package com.server.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.server.domain.user.dto.CoupleDto;
import com.server.domain.user.dto.UserDto;

import jakarta.annotation.PostConstruct;

@Configuration
public class S3UrlInitializer {

    private final S3UrlConfig s3UrlConfig;

    @Autowired
    public S3UrlInitializer(S3UrlConfig s3UrlConfig) {
        this.s3UrlConfig = s3UrlConfig;
    }

    @PostConstruct
    public void init() {
        // Initialize static fields in DTOs
        UserDto userDto = new UserDto(null, null, null, null, null);
        userDto.setS3UrlConfig(s3UrlConfig);

        CoupleDto coupleDto = new CoupleDto();
        coupleDto.setS3UrlConfig(s3UrlConfig);
    }
}
