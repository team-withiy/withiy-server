package com.server.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.user.dto.GetUserOutDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.mapper.UserMapper;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.AuthException;
import com.server.global.error.exception.BusinessException;
import com.server.global.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

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

    public User getUserWithPersonalInfo(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    }

    public GetUserOutDto getUserWithoutPersonalInfo(Long id) {
        User user = getUserWithPersonalInfo(id);
        return userMapper.toGetUserOutDto(user);
    }

    public GetUserOutDto getUserWithoutPersonalInfo(String nickname) {
        User user = getUserWithPersonalInfo(nickname);
        return userMapper.toGetUserOutDto(user);
    }

    public String deleteUser(HttpServletRequest request) {
        Long id = jwtService.extractUserId(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        User user = getUserWithPersonalInfo(id);
        userRepository.delete(user);
        return user.getNickname();
    }

    public GetUserOutDto getUser(HttpServletRequest request) {
        Long id = jwtService.extractUserId(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        log.info("Extracted username: {}", id);
        return userMapper.toGetUserOutDto(getUserWithPersonalInfo(id));

    }
}
