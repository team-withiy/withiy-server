package com.server.global.service;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 테스트 환경에서 사용할 S3Service의 모의 구현체 실제 AWS S3를 사용하지 않고 가상의 URL을 반환
 */
@Service
@Primary
@Profile("test")
@Slf4j
public class MockS3Service {

	@Value("${aws.s3.bucket:bucket}")
	private String bucket;

	@Value("${aws.s3.url}")
	private String s3Url;

	@Value("${aws.s3.cloudfront-url}")
	private String cloudfrontUrl;

	/**
	 * 테스트용 가상 이미지 URL 생성 (CloudFront URL 형식)
	 */
	public String uploadImage(MultipartFile file, String directory) {
		log.info("Mock S3 Service: Uploading image to directory: {}", directory);
		String fileName = directory + "/" + UUID.randomUUID().toString() + ".jpg";
		return cloudfrontUrl + fileName;
	}

	/**
	 * 테스트 환경에서는 아무 작업도 수행하지 않음
	 */
	public void deleteImage(String imageUrl) {
		log.info("Mock S3 Service: Deleting image: {}", imageUrl);
		// 테스트 환경에서는 실제 삭제 작업 없음
	}

	/**
	 * 객체 URL 생성 (테스트용 CloudFront URL)
	 */
	public String getObjectUrl(String objectKey) {
		return cloudfrontUrl + objectKey;
	}

	/**
	 * 객체 존재 여부 확인 (테스트용)
	 */
	public boolean doesObjectExist(String objectKey) {
		return true; // 테스트에서는 항상 존재한다고 가정
	}
}
