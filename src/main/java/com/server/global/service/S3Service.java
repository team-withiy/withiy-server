package com.server.global.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    /**
     * 이미지 파일을 S3에 업로드
     * 
     * @param file 업로드할 이미지 파일
     * @param directory 업로드할 디렉토리 (예: "profile", "course", 등)
     * @return 업로드된 이미지의 URL
     */
    public String uploadImage(MultipartFile file, String directory) {
        try {
            // 파일 확장자 추출
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 고유한 파일명 생성
            String fileName = directory + "/" + UUID.randomUUID().toString() + extension;

            // 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // S3에 업로드
            s3Client.putObject(
                    new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));

            // 업로드된 파일의 URL 생성
            String imageUrl = s3Client.getUrl(bucket, fileName).toString();
            log.info("Image uploaded successfully: {}", imageUrl);

            return imageUrl;

        } catch (IOException e) {
            log.error("Failed to upload image to S3", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    /**
     * S3에서 이미지 삭제
     * 
     * @param imageUrl 삭제할 이미지의 URL
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // URL에서 key 추출
            String key = imageUrl.substring(imageUrl.indexOf(bucket) + bucket.length() + 1);

            // S3에서 이미지 삭제
            s3Client.deleteObject(new DeleteObjectRequest(bucket, key));
            log.info("Image deleted successfully: {}", imageUrl);

        } catch (Exception e) {
            log.error("Failed to delete image from S3", e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }
}
