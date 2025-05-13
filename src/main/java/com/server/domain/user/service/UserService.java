package com.server.domain.user.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
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

    @Transactional
    public String deleteUser(User user, boolean forAccountWithdrawal) {
        String originalNickname = user.getNickname();

        if (forAccountWithdrawal) { // True: User wants to withdraw their account (e.g., DELETE
                                    // /api/users/me)
            // Soft delete: set deletedAt to current time, mark for eventual hard deletion
            user.setDeletedAt(LocalDateTime.now());
            user.updateRefreshToken(null); // Clear refresh token upon withdrawal
            userRepository.save(user);
            log.info("User account '{}' marked for deletion (soft delete).", originalNickname);
        } else { // False: User wants to "Start Anew" (e.g., POST /api/users/restore with restore:
                 // false)
            // Reset the account for re-registration, do not hard delete.
            // 1. Set deletedAt to signify the account is in a reset/inactive state.
            user.setDeletedAt(LocalDateTime.now());

            // 2. Reset all term agreements to false
            if (user.getTermAgreements() != null) {
                for (TermAgreement agreement : user.getTermAgreements()) {
                    agreement.setAgreed(false);
                    termAgreementRepository.save(agreement);
                }
                log.debug("Reset term agreements for user '{}'.", originalNickname);
            }

            // 3. Reset user-specific profile data
            user.setThumbnail(null); // Clear profile picture
            user.updateRefreshToken(null); // Clear refresh token

            userRepository.save(user); // Save the updated user entity
            log.info("User account '{}' has been reset for re-registration.", originalNickname);
        }
        return originalNickname; // Return original nickname, controller can decide on response
                                 // message
    }

    private boolean areAllRequiredTermsAgreed(User user) {
        // If there are no term agreements, return false
        if (user.getTermAgreements() == null || user.getTermAgreements().isEmpty()) {
            return false;
        }

        for (TermAgreement agreement : user.getTermAgreements()) {
            if (agreement.getTerm().isRequired() && !agreement.isAgreed()) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public String registerUser(User user, Map<Long, Boolean> termAgreements, String nickname) {
        if (user == null) {
            throw new BusinessException(UserErrorCode.NOT_FOUND);
        }

        if (termAgreements == null || termAgreements.isEmpty()) {
            log.error("Term agreements map is empty or null");
            throw new BusinessException(UserErrorCode.INVALID_PARAMETER);
        }

        // 닉네임 설정 (제공된 경우)
        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname);
            log.debug("Updated nickname for user ID {}: {}", user.getId(), nickname);
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
        log.info("Updated user information and term agreements for user: {}", user.getNickname());
        return user.getNickname();
    }

    @Transactional
    public String registerUser(User user, Map<Long, Boolean> termAgreements) {
        return registerUser(user, termAgreements, null);
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
}
