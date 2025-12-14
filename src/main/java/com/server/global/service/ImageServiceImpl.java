package com.server.global.service;

import com.server.global.dto.ImageResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 이미지 업로드 및 관리를 위한 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
	private final S3Service s3Service;
	private final WebClient webClient;

	/**
	 * 이미지 파일을 업로드하고 URL을 반환
	 *
	 * @param file       업로드할 이미지 파일
	 * @param entityType 이미지를 사용하는 엔티티 타입 (예: "user", "place", "route")
	 * @param entityId   엔티티의 ID (null 가능, 새로운 엔티티의 경우)
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

	//여러장 이미지 업로드
	@Override
	public List<ImageResponseDto> uploadImages(List<MultipartFile> files, String entityType,
		Long entityId) {
		if (files == null || files.isEmpty()) {
			throw new IllegalArgumentException("파일 목록이 비어 있습니다.");
		}

		List<ImageResponseDto> responseList = new ArrayList<>();

		for (MultipartFile file : files) {
			if (!validateImage(file)) {
				throw new IllegalArgumentException(getValidationErrorMessage(file));
			}
			responseList.add(uploadImage(file, entityType, entityId));
		}

		return responseList;
	}

	@Override
	public MultipartFile downloadImage(String pictureUrl) {
		try {
			// 1. 이미지 다운로드
			byte[] imageBytes = webClient.get()
				.uri(pictureUrl)
				.retrieve()
				.bodyToMono(byte[].class)
				.block();

			if (imageBytes == null || imageBytes.length == 0) {
				log.warn("이미지 바이트가 null이거나 비어 있음: {}", pictureUrl);
				return null;
			}

			// 2. 확장자 추출 (기본 jpg)
			String extension = getImageExtension(pictureUrl);
			String filename = UUID.randomUUID() + "." + extension;

			// 3. MultipartFile 생성
			return new MockMultipartFile(
				"file",                 // name
				filename,               // originalFilename
				"image/" + extension,   // contentType
				imageBytes              // content
			);

		} catch (Exception e) {
			log.error("이미지 다운로드 중 오류 발생: {}", pictureUrl, e);
			return null;
		}
	}

	private String getImageExtension(String url) {
		try {
			String noQuery = url.split("\\?")[0]; // 쿼리 파라미터 제거
			String ext = noQuery.substring(noQuery.lastIndexOf('.') + 1).toLowerCase();
			if (ext.matches("jpg|jpeg|png|gif|bmp|webp")) {
				return ext;
			}
		} catch (Exception ignored) {
		}
		return "jpg"; // 기본 확장자 fallback
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
