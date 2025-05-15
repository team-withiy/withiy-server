package com.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * 테스트 환경에서 사용할 S3 설정 클래스
 */
@Configuration
@Profile("test") // 테스트 환경에서만 활성화
public class TestS3Config {

    /**
     * 테스트 환경에서 사용할 S3 클라이언트 실제 AWS S3 연결 대신 고정된 리전 값으로 설정
     */
    @Bean
    @Primary
    public AmazonS3 s3TestClient() {
        // 테스트용 더미 자격 증명
        AWSCredentials credentials = new BasicAWSCredentials("test-key", "test-secret");

        // 테스트용 S3 클라이언트 (서울 리전 고정)
        return AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }
}
