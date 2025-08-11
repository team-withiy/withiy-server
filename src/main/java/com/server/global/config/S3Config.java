package com.server.global.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test") // 테스트 환경이 아닐 때만 활성화
public class S3Config {

	@Value("${aws.s3.access-key}")
	private String accessKey;

	@Value("${aws.s3.secret-key}")
	private String secretKey;

	@Value("${aws.s3.region}")
	private String region;

	@Bean
	public AmazonS3 s3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		try {
			return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region))
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		} catch (IllegalArgumentException e) {
			// 유효하지 않은 리전 값에 대한 대체 처리
			return AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2) // 기본 서울 리전
				// 사용
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		}
	}
}
