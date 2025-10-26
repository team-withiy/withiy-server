package com.server.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.domain.term.entity.Term;
import com.server.domain.user.dto.CoupleDto;
import com.server.domain.user.dto.RestoreCoupleDto;
import com.server.domain.user.entity.Couple;
import com.server.domain.user.entity.CoupleMember;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.CoupleMemberRepository;
import com.server.domain.user.repository.CoupleRepository;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.CoupleErrorCode;
import com.server.global.error.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CoupleServiceTest {

	@Mock
	private CoupleRepository coupleRepository;

	@Mock
	private CoupleMemberRepository coupleMemberRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CoupleService coupleService;

	private User user1;
	private User user2;
	private Couple couple;
	private CoupleMember user1Member;
	private CoupleMember user2Member;
	private LocalDate firstMetDate;
	private LocalDateTime createdDateTime;

	@BeforeEach
	void setUp() {
		// Create empty terms list to avoid NullPointerException
		ArrayList<Term> emptyTerms = new ArrayList<>();

		// Setup users
		user1 = User.builder().nickname("User1").thumbnail("user1_thumbnail.jpg").code("USER1_CODE")
			.terms(emptyTerms) // Add empty terms list instead of null
			.build();
		ReflectionTestUtils.setField(user1, "id", 1L);

		user2 = User.builder().nickname("User2").thumbnail("user2_thumbnail.jpg").code("USER2_CODE")
			.terms(emptyTerms) // Add empty terms list instead of null
			.build();
		ReflectionTestUtils.setField(user2, "id", 2L);

		// Setup dates
		firstMetDate = LocalDate.of(2025, 1, 1);
		createdDateTime = LocalDateTime.of(2025, 4, 29, 12, 0);

		// Setup couple
		couple = new Couple(firstMetDate);
		ReflectionTestUtils.setField(couple, "id", 1L);
		ReflectionTestUtils.setField(couple, "createdAt", createdDateTime);
		ReflectionTestUtils.setField(couple, "updatedAt", createdDateTime);

		user1Member = CoupleMember.builder()
			.couple(couple)
			.user(user1)
			.build();
		ReflectionTestUtils.setField(user1Member, "id", 1L);
		user2Member = CoupleMember.builder()
			.couple(couple)
			.user(user2)
			.build();
		ReflectionTestUtils.setField(user2Member, "id", 2L);
	}

	@Test
	@DisplayName("Connect couple - successful connection")
	void connectCoupleSuccessTest() {
		// Setup
		when(userRepository.findByCode("USER2_CODE")).thenReturn(Optional.of(user2));

		// Mock the save method to set created date before returning
		when(coupleRepository.save(any(Couple.class))).thenAnswer(invocation -> {
			Couple savedCouple = invocation.getArgument(0);
			if (savedCouple.getCreatedAt() == null) {
				savedCouple.updateDeletedAt(createdDateTime);
			}
			ReflectionTestUtils.setField(savedCouple, "id", 1L);
			return savedCouple;
		});

		// Call the method
		CoupleDto result = coupleService.connectCouple(user1, "USER2_CODE", firstMetDate);

		// Verify
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("User2", result.getPartnerNickname());
		assertEquals("user2_thumbnail.jpg", result.getPartnerThumbnail());
		verify(coupleRepository).save(any(Couple.class));
	}

	@Test
	@DisplayName("Connect couple - user already connected")
	void connectCoupleUserAlreadyConnectedTest() {
		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.of(user1Member));

		// Call and verify
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			coupleService.connectCouple(user1, "USER2_CODE", firstMetDate);
		});

		assertEquals(CoupleErrorCode.COUPLE_ALREADY_CONNECTED, exception.getErrorCode());
	}

	@Test
	@DisplayName("Connect couple - partner not found")
	void connectCouplePartnerNotFoundTest() {
		// Setup
		when(userRepository.findByCode("NONEXISTENT_CODE")).thenReturn(Optional.empty());

		// Call and verify
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			coupleService.connectCouple(user1, "NONEXISTENT_CODE", firstMetDate);
		});

		assertEquals(CoupleErrorCode.PARTNER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("Connect couple - self connection attempt")
	void connectCoupleSelfConnectionTest() {
		// Setup
		when(userRepository.findByCode("USER1_CODE")).thenReturn(Optional.of(user1));

		// Call and verify
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			coupleService.connectCouple(user1, "USER1_CODE", firstMetDate);
		});

		assertEquals(CoupleErrorCode.SELF_CONNECTION_NOT_ALLOWED, exception.getErrorCode());
	}

	@Test
	@DisplayName("Connect couple - partner already connected")
	void connectCouplePartnerAlreadyConnectedTest() {
		// Setup
		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(Optional.empty());
		when(userRepository.findByCode("USER2_CODE")).thenReturn(Optional.of(user2));
		when(coupleMemberRepository.findByUserId(user2.getId())).thenReturn(
			Optional.of(user2Member));

		// Call and verify
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			coupleService.connectCouple(user1, "USER2_CODE", firstMetDate);
		});

		assertEquals(CoupleErrorCode.PARTNER_ALREADY_CONNECTED, exception.getErrorCode());
	}

	@Test
	@DisplayName("Get couple - successful retrieval")
	void getCoupleSuccessTest() {
		// Setup
		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.of(user1Member));

		when(coupleMemberRepository.findByCoupleIdAndUserIdNot(couple.getId(), user1.getId()))
			.thenReturn(Optional.of(user2Member));

		// Call the method
		CoupleDto result = coupleService.getCouple(user1);

		// Verify
		assertNotNull(result);
		assertFalse(result.getRestoreEnabled());
		assertEquals(1L, result.getId());
		assertEquals("User2", result.getPartnerNickname());
		assertEquals("user2_thumbnail.jpg", result.getPartnerThumbnail());
		assertEquals(firstMetDate, result.getFirstMetDate());
	}

	@Test
	@DisplayName("Get couple - successful retrieval with restore enabled")
	void getCoupleSuccessWithRestoreEnabledTest() {
		// Setup
		couple.updateDeletedAt(LocalDateTime.now().minusDays(1)); // Simulate deleted couple

		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.of(user1Member));

		when(coupleMemberRepository.findByCoupleIdAndUserIdNot(couple.getId(), user1.getId()))
			.thenReturn(Optional.of(user2Member));

		// Call the method
		CoupleDto result = coupleService.getCouple(user1);

		// Verify
		assertNotNull(result);
		assertTrue(result.getRestoreEnabled());
		assertEquals(1L, result.getId());
		assertEquals("User2", result.getPartnerNickname());
		assertEquals("user2_thumbnail.jpg", result.getPartnerThumbnail());
		assertEquals(firstMetDate, result.getFirstMetDate());
	}


	@Test
	@DisplayName("Get couple - not found")
	void getCoupleNotFoundTest() {
		// Setup
		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.empty());

		// Call and verify
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			coupleService.getCouple(user1);
		});

		assertEquals(CoupleErrorCode.COUPLE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("Disconnect couple - successful disconnection")
	void disconnectCoupleSuccessTest() {
		// Setup
		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.of(user1Member));

		// Call the method
		Long result = coupleService.disconnectCouple(user1);

		// Verify
		assertEquals(1L, result);
		assertNotNull(couple.getDeletedAt());
		verify(coupleRepository).save(couple);
	}

	@Test
	@DisplayName("Disconnect couple - not found")
	void disconnectCoupleNotFoundTest() {
		// Setup
		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.empty());

		// Call and verify
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			coupleService.disconnectCouple(user1);
		});

		assertEquals(CoupleErrorCode.COUPLE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("Update first met date - successful update")
	void updateFirstMetDateSuccessTest() {
		// Setup
		LocalDate newDate = LocalDate.of(2024, 12, 25);
		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.of(user1Member));
		when(coupleRepository.save(couple)).thenReturn(couple);

		// Call the method
		CoupleDto result = coupleService.updateFirstMetDate(user1, newDate);

		// Verify
		assertNotNull(result);
		assertEquals(newDate, couple.getFirstMetDate());
		verify(coupleRepository).save(couple);
	}

	@Test
	@DisplayName("Update first met date - couple not found")
	void updateFirstMetDateNotFoundTest() {
		// Setup
		LocalDate newDate = LocalDate.of(2024, 12, 25);
		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.empty());

		// Call and verify
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			coupleService.updateFirstMetDate(user1, newDate);
		});

		assertEquals(CoupleErrorCode.COUPLE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("Restore couple - successful restoration with restore flag true")
	void RestoreCoupleSuccessWithRestoreTrueTest() {
		// Setup
		RestoreCoupleDto requestDto = new RestoreCoupleDto(true);
		couple.updateDeletedAt(LocalDateTime.now().minusDays(1));

		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.of(user1Member));

		// Call the method
		Long result = coupleService.restoreCouple(user1, requestDto.isRestore());

		// Verify
		assertEquals(1L, result);
		assertNull(couple.getDeletedAt());
		assertEquals(couple.getFirstMetDate(), LocalDate.of(2025, 1, 1));
		verify(coupleRepository, never()).delete(couple);
		verify(coupleRepository).save(couple);
	}


	@Test
	@DisplayName("Restore couple - successful restoration with restore flag false")
	void RestoreCoupleSuccessWithRestoreFalseTest() {
		// Setup
		RestoreCoupleDto requestDto = new RestoreCoupleDto(false);
		couple.updateDeletedAt(LocalDateTime.now().minusDays(1));
		when(coupleMemberRepository.findByUserId(user1.getId())).thenReturn(
			Optional.of(user1Member));

		// Verify
		// When
		Long result = coupleService.restoreCouple(user1, requestDto.isRestore());

		// Then
		assertEquals(1L, result); // 반환은 여전히 couple ID
		verify(coupleRepository).delete(couple);
		verify(coupleRepository, never()).save(any());
	}
}
