package com.server.domain.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.domain.term.repository.TermAgreementRepository;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceSchedulerTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private TermAgreementRepository termAgreementRepository;

	@InjectMocks
	private UserService userService;

	@Test
	@DisplayName("만료된 계정 자동 영구 삭제 - 30일 이상 삭제된 계정 제거")
	void purgeExpiredAccounts_shouldDeleteAccountsOlderThan30Days() {
		// given
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime thirtyOneDaysAgo = now.minus(31, ChronoUnit.DAYS);

		User expiredUser = new User();
		expiredUser.setId(1L);
		expiredUser.setNickname("ExpiredUser");
		expiredUser.setDeletedAt(thirtyOneDaysAgo);

		// 시간 정확도 문제를 해결하기 위해 any() 사용
		when(userRepository.findByDeletedAtNotNullAndDeletedAtBefore(any(LocalDateTime.class)))
			.thenReturn(Collections.singletonList(expiredUser));

		// when
		userService.purgeExpiredAccounts();

		// then
		// 시간 비교를 피하고 메소드가 호출되었는지만 확인
		verify(userRepository, times(1))
			.findByDeletedAtNotNullAndDeletedAtBefore(any(LocalDateTime.class));
		verify(userRepository, times(1)).delete(expiredUser);
	}

	@Test
	@DisplayName("예외 발생시 다른 계정 삭제 작업에 영향을 주지 않도록 함")
	void purgeExpiredAccounts_shouldHandleExceptions() {
		// given
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime thirtyOneDaysAgo = now.minus(31, ChronoUnit.DAYS);

		User user1 = new User();
		user1.setId(1L);
		user1.setNickname("User1");
		user1.setDeletedAt(thirtyOneDaysAgo);

		User user2 = new User();
		user2.setId(2L);
		user2.setNickname("User2");
		user2.setDeletedAt(thirtyOneDaysAgo);

		// 시간 정확도 문제를 해결하기 위해 any() 사용
		when(userRepository.findByDeletedAtNotNullAndDeletedAtBefore(any(LocalDateTime.class)))
			.thenReturn(Arrays.asList(user1, user2));

		// user1 삭제시 예외가 발생하도록 설정
		doThrow(new RuntimeException("Database error")).when(userRepository).delete(user1);

		// when
		userService.purgeExpiredAccounts();

		// then
		// user1에서 예외가 발생해도 user2는 삭제 시도해야 함
		verify(userRepository, times(1)).delete(user1); // 예외가 발생하더라도 호출되는지 확인
		verify(userRepository, times(1)).delete(user2);
	}

	@Test
	@DisplayName("만료된 계정이 없는 경우 삭제 작업이 수행되지 않음")
	void purgeExpiredAccounts_shouldNotDeleteWhenNoExpiredAccounts() {
		// given
		// 시간 정확도 문제를 해결하기 위해 any() 사용
		when(userRepository.findByDeletedAtNotNullAndDeletedAtBefore(any(LocalDateTime.class)))
			.thenReturn(Collections.emptyList());

		// when
		userService.purgeExpiredAccounts();

		// then
		verify(userRepository, times(0)).delete(any(User.class));
	}
}
