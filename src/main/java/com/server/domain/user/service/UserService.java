package com.server.domain.user.service;

import com.server.domain.folder.service.FolderService;
import com.server.domain.oauth.repository.OAuthRepository;
import com.server.domain.term.entity.TermAgreement;
import com.server.domain.term.service.TermService;
import com.server.domain.user.dto.ActiveCoupleDto;
import com.server.domain.user.dto.NotificationSettingRequestDto;
import com.server.domain.user.dto.ProfileResponseDto;
import com.server.domain.user.dto.RestorableCoupleDto;
import com.server.domain.user.dto.UserDto;
import com.server.domain.user.dto.UserNotificationSettingResponseDto;
import com.server.domain.user.dto.UserProfileResponseDto;
import com.server.domain.user.entity.Couple;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.BusinessException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	// 계정 복구 후 유효 기간 (30일)
	private static final long ACCOUNT_RESTORATION_PERIOD_DAYS = 30;
	private final UserRepository userRepository;
	private final OAuthRepository oauthRepository;
	private final TermService termService;
	private final CoupleService coupleService;
	private final FolderService folderService;

	@Transactional
	public void saveRefreshToken(Long id, String refreshToken) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
		user.updateRefreshToken(refreshToken);
		userRepository.save(user);
	}

	@Transactional
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public User getUserWithPersonalInfo(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public UserDto getUser(User user) {

		// 약관 동의 여부 확인
		List<TermAgreement> agreements = termService.getUserTermAgreements(user.getId());
		boolean isRegistered = hasAgreedToAllRequiredTerms(user.getId(), agreements);

		// 1. 커플 유저 조회
		Couple couple = coupleService.getCoupleOrNull(user);
		if (couple == null) {
			return UserDto.from(user, isRegistered); // 커플 없음
		}

		// 2. 파트너 유저 조회
		User partner = coupleService.getPartner(couple, user);

		// 3. 커플 상태에 따른 DTO 반환
		if (couple.getDeletedAt() == null) {
			return UserDto.from(user, ActiveCoupleDto.from(couple, partner), isRegistered);
		} else if (couple.isRestorable()) {
			return UserDto.from(user, RestorableCoupleDto.from(couple, partner), isRegistered);
		}

		return UserDto.from(user, isRegistered); // 삭제된 지 30일 이상
	}

	private boolean hasAgreedToAllRequiredTerms(Long userId, List<TermAgreement> agreements) {
		if (agreements.isEmpty()) {
			log.warn("User {} has no term agreements.", userId);
			return false;
		}

		// 필수 약관 중 하나라도 미동의가 있다면 false
		boolean allAgreed = agreements.stream()
			.filter(a -> a.getTerm().isRequired())
			.allMatch(TermAgreement::isAgreed);

		if (!allAgreed) {
			log.info("User {} has not agreed to all required terms.", userId);
		}

		return allAgreed;
	}


	@Transactional
	public String deleteUser(User user, boolean forAccountWithdrawal) {
		String originalNickname = user.getNickname();

		if (forAccountWithdrawal) { // True: User wants to withdraw their account (e.g., DELETE
			// /api/users/me)
			// Soft delete: set deletedAt to current time, mark for eventual hard deletion
			user.updateDeletedAt(LocalDateTime.now());
			user.updateRefreshToken(null); // Clear refresh token upon withdrawal
			userRepository.save(user);
			log.info("User account '{}' marked for deletion (soft delete).", originalNickname);
		} else { // False: User wants to "Start Anew" (e.g., POST /api/users/restore with restore:
			// false)

			// 1. Clear soft delete timestamp
			user.updateDeletedAt(null);

			// 2. Reset all term agreements to false
			List<TermAgreement> agreements = termService.getUserTermAgreements(user.getId());
			agreements.forEach(agreement -> agreement.setAgreed(false));
			termService.saveAllTermAgreements(agreements);
			log.debug("Reset term agreements for user '{}'.", originalNickname);

			// 3. Reset user-specific profile data
			oauthRepository.findByUser(user).ifPresent(oauth -> {
				user.updateThumbnail(oauth.getThumbnail());
				user.updateNickname(oauth.getNickname());
				oauthRepository.save(oauth);
				log.debug("Reset OAuth profile data for user '{}'.", originalNickname);
			});

			user.updateRefreshToken(null); // Clear refresh token
			userRepository.save(user); // Save the updated user entity
			log.info("User account '{}' has been reset for re-registration.", originalNickname);
		}

		return originalNickname; // Return original nickname, controller can decide on response
		// message
	}

	private boolean areAllRequiredTermsAgreed(User user) {
		// If there are no term agreements, return false
		List<TermAgreement> agreements = termService.getUserTermAgreements(user.getId());

		// 약관이 없으면 바로 false
		if (agreements.isEmpty()) {
			return false;
		}

		// 필수 약관 모두 동의했는지 검사
		return agreements.stream()
			.filter(a -> a.getTerm().isRequired())
			.allMatch(TermAgreement::isAgreed);
	}

	@Transactional
	public String registerUser(User user, Map<Long, Boolean> termAgreements, String nickname,
		String thumbnail) {
		if (user == null) {
			throw new BusinessException(UserErrorCode.NOT_FOUND);
		}

		if (termAgreements == null || termAgreements.isEmpty()) {
			log.error("Term agreements map is empty or null");
			throw new BusinessException(UserErrorCode.INVALID_PARAMETER);
		}

		// 닉네임 설정 (제공된 경우)
		if (nickname != null && !nickname.trim().isEmpty()) {
			user.updateNickname(nickname);
			log.debug("Updated nickname for user ID {}: {}", user.getId(), nickname);
		}

		// 프로필 이미지 설정 (제공된 경우)
		if (thumbnail != null && !thumbnail.trim().isEmpty()) {
			// Consider adding URL validation here for security and data integrity.
			user.updateThumbnail(thumbnail);
			log.debug("Updated thumbnail for user ID {}: {}", user.getId(), thumbnail);
		}

		// Update user's term agreements based on client request
		List<TermAgreement> existingAgreements = termService.getUserTermAgreements(user.getId());
		existingAgreements.forEach(agreement -> {
			Long termId = agreement.getTerm().getId();
			if (termAgreements.containsKey(termId)) {
				Boolean agreed = termAgreements.get(termId);
				agreement.setAgreed(agreed);
				log.debug("Updated term agreement for term ID {}: {}", termId, agreed);
			}
		});
		termService.saveAllTermAgreements(existingAgreements);

		// Check if all required terms are agreed to and log the registration status
		boolean registered = areAllRequiredTermsAgreed(user);
		if (registered) {
			log.info("All required terms agreed to, user is registered: {}", user.getNickname());
		} else {
			log.info("Not all required terms agreed to, user is not fully registered: {}",
				user.getNickname());
		}

		userRepository.save(user);
		log.info("Updated user information and term agreements for user: {}", user.getNickname());

		// 사용자 등록 후 기본 폴더 생성
		folderService.createDefaultFolder(user);
		return user.getNickname();
	}

	@Transactional
	public String registerUser(User user, Map<Long, Boolean> termAgreements) {
		return registerUser(user, termAgreements, null, null);
	}

	@Transactional
	public String restoreAccount(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

		// 이미 활성화된 계정인지 확인
		if (user.getDeletedAt() == null) {
			throw new BusinessException(UserErrorCode.ALREADY_ACTIVE);
		}

		// 복구 가능 기간 확인 (30일)
		LocalDateTime deletedAt = user.getDeletedAt();
		LocalDateTime currentTime = LocalDateTime.now();
		long daysSinceDeleted = ChronoUnit.DAYS.between(deletedAt, currentTime);

		if (daysSinceDeleted > ACCOUNT_RESTORATION_PERIOD_DAYS) {
			throw new BusinessException(UserErrorCode.RESTORATION_PERIOD_EXPIRED);
		}

		// 계정 복구 처리
		user.updateDeletedAt(null);
		userRepository.save(user);
		log.info("User account restored successfully: {}", user.getNickname());

		return user.getNickname();
	}

	/**
	 * 매일 새벽 3시에 복구 기간이 만료된 삭제된 계정을 영구 삭제하는 스케줄링 작업 Cron 표현식: 초 분 시 일 월 요일
	 */
	@Scheduled(cron = "0 0 3 * * ?")
	@Transactional
	public void purgeExpiredAccounts() {
		log.info("Starting scheduled task to purge expired user accounts");
		try {
			// 복구 기간(30일)이 지난 계정 계산
			LocalDateTime expirationThreshold =
				LocalDateTime.now().minus(ACCOUNT_RESTORATION_PERIOD_DAYS, ChronoUnit.DAYS);

			// 만료된 계정 목록 조회
			List<User> expiredUsers =
				userRepository.findByDeletedAtNotNullAndDeletedAtBefore(expirationThreshold);

			if (expiredUsers.isEmpty()) {
				log.info("No expired accounts found for permanent deletion");
				return;
			}

			log.info("Found {} expired user accounts scheduled for permanent deletion",
				expiredUsers.size());

			// 만료된 계정 영구 삭제
			for (User user : expiredUsers) {
				try {
					log.info(
						"Permanently deleting expired user account: ID={}, Nickname={}, DeletedAt={}",
						user.getId(), user.getNickname(), user.getDeletedAt());
					userRepository.delete(user);
				} catch (Exception e) {
					log.error("Error while deleting expired user account ID={}: {}", user.getId(),
						e.getMessage(), e);
				}
			}

			log.info("Completed purging expired user accounts. Total accounts processed: {}",
				expiredUsers.size());
		} catch (Exception e) {
			log.error("Error during scheduled account purge task: {}", e.getMessage(), e);
		}
	}

	/**
	 * 사용자 프로필 이미지 업데이트
	 *
	 * @param user      현재 사용자
	 * @param nickname  새 닉네임
	 * @param thumbnail 새 프로필 이미지 URL
	 * @return 업데이트된 프로필 정보가 포함된 DTO
	 */
	@Transactional
	public ProfileResponseDto updateProfile(User user, String nickname, String thumbnail) {
		if (user == null) {
			throw new BusinessException(UserErrorCode.NOT_FOUND);
		}

		// 사용자 정보 업데이트
		nickname = updateIfNotBlank(nickname, user.getNickname(), "nickname");
		thumbnail = updateIfNotBlank(thumbnail, user.getThumbnail(), "thumbnail");

		user.updateNickname(nickname);
		user.updateThumbnail(thumbnail);
		userRepository.save(user);

		// 응답 DTO 생성
		return new ProfileResponseDto(nickname, thumbnail);
	}

	private String updateIfNotBlank(String newValue, String currentValue, String fieldName) {
		if (newValue == null || newValue.trim().isEmpty()) {
			log.warn("{} is null or empty, using existing value: {}", fieldName, currentValue);
			return currentValue;
		} else {
			log.info("Updating {} from {} to {}", fieldName, currentValue, newValue);
			return newValue.trim(); // 공백 제거
		}
	}

	/**
	 * 유저 코드로 사용자 프로필 조회
	 *
	 * @param userCode 조회할 사용자의 고유 코드
	 * @return 사용자 프로필 정보
	 */
	@Transactional(readOnly = true)
	public UserProfileResponseDto getUserProfileByCode(String userCode) {
		if (userCode == null || userCode.trim().isEmpty()) {
			log.warn("User code is null or empty");
			throw new BusinessException(UserErrorCode.INVALID_PARAMETER);
		}

		User user = userRepository.findByCode(userCode).orElseThrow(() -> {
			log.warn("User not found with userCode: {}", userCode);
			throw new BusinessException(UserErrorCode.NOT_FOUND);
		});

		// 삭제된 사용자인지 확인
		if (user.getDeletedAt() != null) {
			log.warn("Attempt to access deleted user profile with userCode: {}", userCode);
			throw new BusinessException(UserErrorCode.NOT_FOUND);
		}

		log.info("User profile retrieved for userCode: {}, nickname: {}", userCode,
			user.getNickname());

		// 커플 정보가 있는 경우, 커플 정보도 포함하여 반환
		boolean hasCouple = coupleService.isUserInCouple(user);

		return UserProfileResponseDto.from(user, hasCouple);
	}

	@Transactional
	public void clearRefreshToken(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
		user.updateRefreshToken(null);
		userRepository.save(user);
		log.info("Cleared refresh token for user ID: {}", userId);
	}

	@Transactional
	public void updateNotificationSettings(User user,
		NotificationSettingRequestDto notificationSettingsDto) {
		Boolean updatedDateSetting = updateIfNotNull(
			notificationSettingsDto.getDateNotificationEnabled(), user.getDateNotificationEnabled(),
			"dateNotificationEnabled");
		Boolean updatedEventSetting = updateIfNotNull(
			notificationSettingsDto.getEventNotificationEnabled(),
			user.getEventNotificationEnabled(), "eventNotificationEnabled");
		user.updateDateNotificationEnabled(updatedDateSetting);
		user.updateEventNotificationEnabled(updatedEventSetting);
		userRepository.save(user);
	}

	private Boolean updateIfNotNull(Boolean newValue, Boolean currentValue, String fieldName) {
		if (newValue == null) {
			log.warn("{} is null, using existing value: {}", fieldName, currentValue);
			return currentValue;
		} else {
			log.info("Updating {} from {} to {}", fieldName, currentValue, newValue);
			return newValue;
		}
	}

	@Transactional
	public UserNotificationSettingResponseDto getNotificationSettings(User user) {
		return UserNotificationSettingResponseDto.of(
			user.getId(),
			user.getDateNotificationEnabled(),
			user.getEventNotificationEnabled()
		);
	}
}
