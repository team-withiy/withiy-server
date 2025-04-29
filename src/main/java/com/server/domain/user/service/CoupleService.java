package com.server.domain.user.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.user.dto.CoupleDto;
import com.server.domain.user.entity.Couple;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.CoupleRepository;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.CoupleErrorCode;
import com.server.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;

    /**
     * 사용자 코드로 커플 연결을 시도합니다.
     * 
     * @param user 현재 사용자
     * @param partnerCode 연결할 파트너의 사용자 코드
     * @param firstMetDate 처음 만난 날짜 (선택사항)
     * @return 생성된 커플 정보를 담은 DTO
     */
    @Transactional
    public CoupleDto connectCouple(User user, String partnerCode, LocalDate firstMetDate) {
        // 1. 현재 유저가 이미 커플인지 확인
        if (user.isConnectedCouple()) {
            throw new BusinessException(CoupleErrorCode.ALREADY_CONNECTED);
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
        Couple couple =
                Couple.builder().user1(user).user2(partner).firstMetDate(firstMetDate).build();

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

        Long coupleId = couple.getId();
        coupleRepository.delete(couple);
        log.info("커플이 해제되었습니다. 커플 ID: {}", coupleId);

        return coupleId;
    }
}
