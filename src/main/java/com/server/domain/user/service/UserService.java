package com.server.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.user.dto.GetUserOutDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.mapper.UserMapper;
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

    public GetUserOutDto getUser(User user) {
        return userMapper.toGetUserOutDto(user);
    }

    public String deleteUser(User user) {
        userRepository.delete(user);
        return user.getNickname();
    }

    @Transactional
    public void registerUser(User user, String nickname) {
        user.setNickname(nickname);
        user.setRegistered(true);
        userRepository.save(user);
    }
}
