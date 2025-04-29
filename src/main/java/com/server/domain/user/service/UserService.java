package com.server.domain.user.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.term.entity.TermAgreement;
import com.server.domain.term.repository.TermAgreementRepository;
import com.server.domain.user.dto.UserDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.TermErrorCode;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final TermAgreementRepository termAgreementRepository;

    // 계정 복구 후 유효 기간 (30일)
    private static final long ACCOUNT_RESTORATION_PERIOD_DAYS = 30;

    @Transactional
    public void saveRefreshToken(Long id, String refreshToken) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public User getUserWithPersonalInfo(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    }

    public UserDto getUser(User user) {
        return UserDto.from(user, areAllRequiredTermsAgreed(user));
    }

    public String deleteUser(User user, boolean softDelete) {
        if (softDelete) {
            // Soft delete: set deletedAt to current time
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            // Hard delete: remove the user from the repository
            userRepository.delete(user);
        }
        return user.getNickname();
    }

    private boolean areAllRequiredTermsAgreed(User user) {
        // If there are no term agreements, return false
        if (user.getTermAgreements() == null || user.getTermAgreements().isEmpty()) {
            return false;
        }

        for (TermAgreement agreement : user.getTermAgreements()) {
            if (agreement.getTerm().isRequired() && !agreement.isAgreed()) {
                throw new BusinessException(TermErrorCode.REQUIRED_TERM_NOT_AGREED);
            }
        }
        return true;
    }

    @Transactional
    public String registerUser(User user, Map<Long, Boolean> termAgreements) {
        if (user == null) {
            throw new BusinessException(UserErrorCode.NOT_FOUND);
        }

        if (termAgreements == null || termAgreements.isEmpty()) {
            log.error("Term agreements map is empty or null");
            throw new BusinessException(UserErrorCode.INVALID_PARAMETER);
        }

        // Update each term agreement based on the provided term ID and boolean value
        for (TermAgreement agreement : user.getTermAgreements()) {
            Long termId = agreement.getTerm().getId();
            if (termAgreements.containsKey(termId)) {
                agreement.setAgreed(termAgreements.get(termId));
                termAgreementRepository.save(agreement);
                log.debug("Updated term agreement for term ID {}: {}", termId,
                        termAgreements.get(termId));
            }
        }

        // Check if all required terms are agreed to and log the registration status
        boolean registered = areAllRequiredTermsAgreed(user);
        if (registered) {
            log.info("All required terms agreed to, user is registered: {}", user.getNickname());
        } else {
            log.info("Not all required terms agreed to, user is not fully registered: {}",
                    user.getNickname());
        }

        userRepository.save(user);
        log.info("Updated term agreements for user: {}", user.getNickname());
        return user.getNickname();
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
        user.setDeletedAt(null);
        userRepository.save(user);
        log.info("User account restored successfully: {}", user.getNickname());

        return user.getNickname();
    }
}
