package com.server.domain.user.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.term.entity.Term;
import com.server.domain.term.entity.TermAgreement;
import com.server.domain.term.repository.TermAgreementRepository;
import com.server.domain.term.repository.TermRepository;
import com.server.domain.user.dto.UserDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
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
        return UserDto.from(user);
    }

    public String deleteUser(User user) {
        userRepository.delete(user);
        return user.getNickname();
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

        log.info("Updated term agreements for user: {}", user.getNickname());
        return user.getNickname();
    }
}
