package com.server.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3UrlConfig {

    @Value("${aws.s3.url}")
    private String s3Url;

    @Value("${aws.s3.cloudfront-url}")
    private String cloudfrontUrl;

    public String getS3Url() {
        return s3Url;
    }

    public String getCloudfrontUrl() {
        return cloudfrontUrl;
    }
}
