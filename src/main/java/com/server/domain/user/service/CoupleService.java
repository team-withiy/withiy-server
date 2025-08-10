package com.server.domain.user.service;

import com.server.domain.user.dto.CoupleDto;
import com.server.domain.user.dto.CoupleRestoreStatusDto;
import com.server.domain.user.entity.Couple;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.CoupleRepository;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.CoupleErrorCode;
import com.server.global.error.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoupleService {

	private final CoupleRepository coupleRepository;
	private final UserRepository userRepository;

	/**
	 * 사용자 코드로 커플 연결을 시도합니다.
	 *
	 * @param user         현재 사용자
	 * @param partnerCode  연결할 파트너의 사용자 코드
	 * @param firstMetDate 처음 만난 날짜 (선택사항)
	 * @return 생성된 커플 정보를 담은 DTO
	 */
	@Transactional
	public CoupleDto connectCouple(User user, String partnerCode, LocalDate firstMetDate) {
		// 1. 현재 유저가 이미 커플인지 확인
		if (user.isConnectedCouple()) {
			throw new BusinessException(CoupleErrorCode.COUPLE_ALREADY_CONNECTED);
		}

		// 2. 상대방 유저 조회
		User partner = userRepository.findByCode(partnerCode)
			.orElseThrow(() -> new BusinessException(CoupleErrorCode.PARTNER_NOT_FOUND));

		// 3. 자기 자신과의 연결 시도 방지
		if (user.getId().equals(partner.getId())) {
			throw new BusinessException(CoupleErrorCode.SELF_CONNECTION_NOT_ALLOWED);
		}

		// 4. 상대방이 이미 커플인지 확인
		if (partner.isConnectedCouple()) {
			throw new BusinessException(CoupleErrorCode.PARTNER_ALREADY_CONNECTED);
		}

		// 5. 커플 생성 및 저장
		Couple couple = new Couple(user, partner, firstMetDate);
		coupleRepository.save(couple);
		log.info("새로운 커플이 연결되었습니다. 유저1: {}, 유저2: {}", user.getNickname(), partner.getNickname());

		return CoupleDto.from(couple, user);
	}

	/**
	 * 현재 사용자의 커플 정보를 조회합니다.
	 *
	 * @param user 현재 사용자
	 * @return 커플 정보를 담은 DTO
	 */
	@Transactional(readOnly = true)
	public CoupleDto getCouple(User user) {
		Couple couple = coupleRepository.findByUser1OrUser2(user, user)
			.orElseThrow(() -> new BusinessException(CoupleErrorCode.COUPLE_NOT_FOUND));

		return CoupleDto.from(couple, user);
	}

	@Transactional(readOnly = true)
	public Couple getCoupleOrNull(User user) {
		return coupleRepository.findByUser1OrUser2(user, user)
			.orElse(null);
	}


	/**
	 * 커플 연결을 해제합니다.
	 *
	 * @param user 현재 사용자
	 * @return 해제된 커플의 ID
	 */
	@Transactional
	public Long disconnectCouple(User user) {
		Couple couple = coupleRepository.findByUser1OrUser2(user, user)
			.orElseThrow(() -> new BusinessException(CoupleErrorCode.COUPLE_NOT_FOUND));

		// 이미 해제된 커플인지 확인
		if (couple.getDeletedAt() != null) {
			throw new BusinessException(CoupleErrorCode.COUPLE_ALREADY_DISCONNECTED);
		}

		couple.setDeletedAt(LocalDateTime.now());
		Long coupleId = couple.getId();
		coupleRepository.save(couple);
		log.info("커플이 해제되었습니다. 커플 ID: {}", coupleId);

		return coupleId;
	}

	/**
	 * 커플의 처음 만난 날짜를 업데이트합니다.
	 *
	 * @param user         현재 사용자
	 * @param firstMetDate 설정할 처음 만난 날짜
	 * @return 업데이트된 커플 정보를 담은 DTO
	 */
	@Transactional
	public CoupleDto updateFirstMetDate(User user, LocalDate firstMetDate) {
		// 커플 관계 확인
		Couple couple = coupleRepository.findByUser1OrUser2(user, user)
			.orElseThrow(() -> new BusinessException(CoupleErrorCode.COUPLE_NOT_FOUND));

		// 처음 만난 날짜 업데이트
		couple.setFirstMetDate(firstMetDate);
		coupleRepository.save(couple);

		log.info("커플 ID: {}의 처음 만난 날짜가 {}로 업데이트되었습니다.", couple.getId(), firstMetDate);

		return CoupleDto.from(couple, user);
	}

	/**
	 * 커플 관계를 복구하거나 완전히 삭제합니다.
	 *
	 * @param user    현재 사용자
	 * @param restore 복구 여부 (true: 복구, false: 완전 삭제)
	 * @return 복구된 커플의 ID
	 */
	@Transactional
	public Long restoreCouple(User user, Boolean restore) {
		Couple couple = coupleRepository.findByUser1OrUser2(user, user)
			.orElseThrow(() -> new BusinessException(CoupleErrorCode.COUPLE_NOT_FOUND));

		if (couple.getDeletedAt() == null) {
			throw new BusinessException(CoupleErrorCode.COUPLE_ALREADY_CONNECTED);
		}

		if (Boolean.FALSE.equals(restore)) {
			// 복구하지 않고 커플 정보 완전 삭제
			coupleRepository.delete(couple);
		} else {
			// 복구: deletedAt 초기화
			couple.setDeletedAt(null);
			coupleRepository.save(couple);
		}

		return couple.getId();
	}

	public CoupleRestoreStatusDto getRestoreStatus(User user) {
		// 커플 정보 조회
		Couple couple = coupleRepository.findByUser1OrUser2(user, user)
			.orElseThrow(() -> new BusinessException(CoupleErrorCode.COUPLE_NOT_FOUND));

		// 커플이 삭제된 상태인지 확인
		if (couple.getDeletedAt() == null) {
			throw new BusinessException(CoupleErrorCode.COUPLE_ALREADY_CONNECTED);
		}

		// 삭제된 날짜가 현재 시간으로부터 30일 이내인지 확인
		boolean restoreEnabled = isRestoreEnabled(couple.getDeletedAt());

		return CoupleRestoreStatusDto.builder()
			.coupleId(couple.getId())
			.restoreEnabled(restoreEnabled)
			.deletedAt(couple.getDeletedAt())
			.build();
	}

	private boolean isRestoreEnabled(LocalDateTime deletedAt) {
		return deletedAt.isAfter(LocalDateTime.now().minusDays(30));
	}
}
