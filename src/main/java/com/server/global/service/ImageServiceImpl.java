package com.server.global.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.server.global.dto.ImageResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 이미지 업로드 및 관리를 위한 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final S3Service s3Service;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * 이미지 파일을 업로드하고 URL을 반환
     * 
     * @param file 업로드할 이미지 파일
     * @param entityType 이미지를 사용하는 엔티티 타입 (예: "user", "place", "course")
     * @param entityId 엔티티의 ID (null 가능, 새로운 엔티티의 경우)
     * @return 업로드된 이미지 정보를 담은 DTO
     */
    @Override
    public ImageResponseDto uploadImage(MultipartFile file, String entityType, Long entityId) {
        if (!validateImage(file)) {
            throw new IllegalArgumentException(getValidationErrorMessage(file));
        }

        // 표준화된 디렉토리 경로 생성 (entityType/entityId 또는 entityType/temp)
        String directory = entityType;
        if (entityId != null) {
            directory += "/" + entityId;
        } else {
            directory += "/temp";
        }

        // S3에 이미지 업로드
        String imageUrl = s3Service.uploadImage(file, directory);

        // 응답 DTO 생성
        return ImageResponseDto.builder().imageUrl(imageUrl).entityType(entityType)
                .entityId(entityId).build();
    }

    /**
     * URL로부터 이미지 삭제
     * 
     * @param imageUrl 삭제할 이미지의 URL
     */
    @Override
    public void deleteImage(String imageUrl) {
        s3Service.deleteImage(imageUrl);
    }

    /**
     * 이미지 파일 유효성 검증 (확장자, 크기 등)
     * 
     * @param file 검증할 이미지 파일
     * @return 유효성 검증 결과 (true: 유효, false: 유효하지 않음)
     */
    @Override
    public boolean validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }

        // 파일 형식 검증
        String contentType = file.getContentType();
        if (contentType == null
                || !(contentType.equals("image/jpeg") || contentType.equals("image/png")
                        || contentType.equals("image/jpg") || contentType.equals("image/gif"))) {
            return false;
        }

        return true;
    }

    /**
     * 이미지 파일이 유효하지 않은 이유 반환
     * 
     * @param file 검증할 이미지 파일
     * @return 유효성 검증 실패 이유 (유효한 경우 null)
     */
    @Override
    public String getValidationErrorMessage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "Empty file provided";
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return "File size exceeds 5MB limit";
        }

        String contentType = file.getContentType();
        if (contentType == null
                || !(contentType.equals("image/jpeg") || contentType.equals("image/png")
                        || contentType.equals("image/jpg") || contentType.equals("image/gif"))) {
            return "Only JPEG, PNG, JPG and GIF images are allowed";
        }

        return null;
    }
}
