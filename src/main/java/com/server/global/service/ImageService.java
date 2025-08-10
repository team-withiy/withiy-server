package com.server.global.service;

import com.server.global.dto.ImageResponseDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * 이미지 업로드 및 관리를 위한 서비스 인터페이스
 */
public interface ImageService {

	/**
	 * 이미지 파일을 업로드하고 URL을 반환
	 *
	 * @param file       업로드할 이미지 파일
	 * @param entityType 이미지를 사용하는 엔티티 타입 (예: "user", "place", "course")
	 * @param entityId   엔티티의 ID (null 가능, 새로운 엔티티의 경우)
	 * @return 업로드된 이미지 정보를 담은 DTO
	 */
	ImageResponseDto uploadImage(MultipartFile file, String entityType, Long entityId);

	/**
	 * URL로부터 이미지 삭제
	 *
	 * @param imageUrl 삭제할 이미지의 URL
	 */
	void deleteImage(String imageUrl);

	/**
	 * 이미지 파일 유효성 검증 (확장자, 크기 등)
	 *
	 * @param file 검증할 이미지 파일
	 * @return 유효성 검증 결과 (true: 유효, false: 유효하지 않음)
	 */
	boolean validateImage(MultipartFile file);

	/**
	 * 이미지 파일이 유효하지 않은 이유 반환
	 *
	 * @param file 검증할 이미지 파일
	 * @return 유효성 검증 실패 이유 (유효한 경우 null)
	 */
	String getValidationErrorMessage(MultipartFile file);

	// 여러장의 이미지 업로드
	List<ImageResponseDto> uploadImages(List<MultipartFile> files, String entityType,
		Long entityId);

	MultipartFile downloadImage(String picture);
}
