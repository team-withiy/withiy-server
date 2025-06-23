package com.server.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.server.domain.user.dto.NotificationSettingsDto;
import com.server.domain.user.repository.CoupleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.server.domain.term.entity.Term;
import com.server.domain.term.entity.TermAgreement;
import com.server.domain.term.repository.TermAgreementRepository;
import com.server.domain.user.dto.UserDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.TermErrorCode;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TermAgreementRepository termAgreementRepository;

    @Mock
    private CoupleRepository coupleRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private List<Term> terms;
    private List<TermAgreement> termAgreements;

    @BeforeEach
    void setUp() {
        // Setup Term objects using constructors instead of setters
        terms = new ArrayList<>();
        Term requiredTerm = new Term(1L, "Required Term", "Required term content", true);
        Term optionalTerm = new Term(2L, "Optional Term", "Optional term content", false);

        terms.add(requiredTerm);
        terms.add(optionalTerm);

        // Setup User
        user = User.builder().nickname("testUser").thumbnail("thumbnail.jpg").terms(terms)
                .code("USER123").build();
        user.setId(1L);

        // Setup TermAgreements
        termAgreements = new ArrayList<>();
        for (Term term : terms) {
            // Use the builder pattern for TermAgreement
            TermAgreement agreement = TermAgreement.builder().user(user).term(term).build();
            termAgreements.add(agreement);
        }

        user.setTermAgreements(termAgreements);
    }

    @Test
    @DisplayName("Get user information test")
    void getUserTest() {
        // Setup all required terms as agreed
        for (TermAgreement agreement : user.getTermAgreements()) {
            agreement.setAgreed(true);
        }

        when(coupleRepository.findByUser1OrUser2(any(), any())).thenReturn(Optional.empty());

        // Call the method
        UserDto userDto = userService.getUser(user);

        // Verify the results
        assertNotNull(userDto);
        assertEquals("testUser", userDto.getNickname());
        assertEquals("thumbnail.jpg", userDto.getThumbnail());
        assertEquals("USER123", userDto.getCode());
        assertFalse(userDto.getRestoreEnabled());
        assertTrue(userDto.getIsRegistered());
    }

    @Test
    @DisplayName("Get user with personal info test")
    void getUserWithPersonalInfoTest() {
        // Setup
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Call the method
        User foundUser = userService.getUserWithPersonalInfo(1L);

        // Verify the results
        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals("testUser", foundUser.getNickname());
    }

    @Test
    @DisplayName("Get user with personal info - user not found test")
    void getUserWithPersonalInfoNotFoundTest() {
        // Setup
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Call the method and verify exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserWithPersonalInfo(999L);
        });

        assertEquals(UserErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Soft delete user test")
    void softDeleteUserTest() {
        // Call the method
        String result = userService.deleteUser(user, true);

        // Verify the results
        assertEquals("testUser", result);
        assertNotNull(user.getDeletedAt());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Reset user for re-registration test (formerly hard delete)")
    void resetUserForReRegistrationTest() {
        // Call the method with forAccountWithdrawal = false
        String result = userService.deleteUser(user, false);

        // Verify the results
        assertEquals("testUser", result); // Should return original nickname
        assertNotNull(user.getDeletedAt()); // deletedAt should be set
        assertNull(user.getThumbnail()); // Thumbnail should be cleared
        assertNull(user.getRefreshToken()); // Refresh token should be cleared

        // Verify term agreements are reset
        for (TermAgreement agreement : user.getTermAgreements()) {
            assertFalse(agreement.isAgreed());
            verify(termAgreementRepository).save(agreement); // Verify each agreement is saved
        }

        verify(userRepository).save(user); // User should be saved, not deleted
        verify(userRepository, never()).delete(user); // Ensure delete is not called
    }

    @Test
    @DisplayName("Register user test - successful registration")
    void registerUserSuccessTest() {
        // Setup
        Map<Long, Boolean> termAgreements = new HashMap<>();
        termAgreements.put(1L, true); // Required term
        termAgreements.put(2L, false); // Optional term

        // Call the method
        String result = userService.registerUser(user, termAgreements);

        // Verify the results
        assertEquals("testUser", result);
        verify(termAgreementRepository, times(2)).save(any(TermAgreement.class));
        verify(userRepository).save(user);

        // Verify term agreements were updated
        assertTrue(user.getTermAgreements().get(0).isAgreed());
        assertFalse(user.getTermAgreements().get(1).isAgreed());
    }

    @Test
    @DisplayName("Register user test - null user")
    void registerUserNullUserTest() {
        // Setup
        Map<Long, Boolean> termAgreements = new HashMap<>();
        termAgreements.put(1L, true);

        // Call the method and verify exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.registerUser(null, termAgreements);
        });

        assertEquals(UserErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Register user test - null term agreements")
    void registerUserNullTermAgreementsTest() {
        // Call the method and verify exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.registerUser(user, null);
        });

        assertEquals(UserErrorCode.INVALID_PARAMETER, exception.getErrorCode());
    }

    @Test
    @DisplayName("Restore account test - successful restoration")
    void restoreAccountSuccessTest() {
        // Setup
        user.setDeletedAt(LocalDateTime.now().minusDays(5));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Call the method
        String result = userService.restoreAccount(1L);

        // Verify the results
        assertEquals("testUser", result);
        assertNull(user.getDeletedAt());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Restore account test - already active account")
    void restoreAccountAlreadyActiveTest() {
        // Setup
        user.setDeletedAt(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Call the method and verify exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.restoreAccount(1L);
        });

        assertEquals(UserErrorCode.ALREADY_ACTIVE, exception.getErrorCode());
    }

    @Test
    @DisplayName("Restore account test - restoration period expired")
    void restoreAccountPeriodExpiredTest() {
        // Setup
        user.setDeletedAt(LocalDateTime.now().minusDays(31));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Call the method and verify exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.restoreAccount(1L);
        });

        assertEquals(UserErrorCode.RESTORATION_PERIOD_EXPIRED, exception.getErrorCode());
    }

    @Test
    @DisplayName("Save refresh token test")
    void saveRefreshTokenTest() {
        // Setup
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Call the method
        userService.saveRefreshToken(1L, "newRefreshToken");

        // Verify the results
        assertEquals("newRefreshToken", user.getRefreshToken());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Register user test with nickname update")
    void registerUserWithNicknameTest() {
        // Setup
        Map<Long, Boolean> termAgreements = new HashMap<>();
        termAgreements.put(1L, true); // Required term
        termAgreements.put(2L, false); // Optional term
        String newNickname = "newNickname";

        // Call the method
        String result = userService.registerUser(user, termAgreements, newNickname, null);

        // Verify the results
        assertEquals(newNickname, result);
        assertEquals(newNickname, user.getNickname());
        verify(termAgreementRepository, times(2)).save(any(TermAgreement.class));
        verify(userRepository).save(user);

        // Verify term agreements were updated
        assertTrue(user.getTermAgreements().get(0).isAgreed());
        assertFalse(user.getTermAgreements().get(1).isAgreed());
    }

    @Test
    @DisplayName("Register user test with empty nickname")
    void registerUserWithEmptyNicknameTest() {
        // Setup
        Map<Long, Boolean> termAgreements = new HashMap<>();
        termAgreements.put(1L, true); // Required term
        String originalNickname = user.getNickname();
        String emptyNickname = "   ";
        // Call the method
        String result = userService.registerUser(user, termAgreements, emptyNickname, null);

        // Verify that the nickname remains unchanged
        assertEquals(originalNickname, result);
        assertEquals(originalNickname, user.getNickname());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Register user test with null nickname")
    void registerUserWithNullNicknameTest() {
        // Setup
        Map<Long, Boolean> termAgreements = new HashMap<>();
        termAgreements.put(1L, true); // Required term
        String originalNickname = user.getNickname();

        // Call the method
        String result = userService.registerUser(user, termAgreements, null, null);

        // Verify that the nickname remains unchanged
        assertEquals(originalNickname, result);
        assertEquals(originalNickname, user.getNickname());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Set refresh token null test - logout user")
    void logoutUserTest() {
        // given
        user.setRefreshToken("dummy-refresh-token");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        userService.clearRefreshToken(1L);

        // then
        assertNull(user.getRefreshToken());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Set new profile test - set nickname and thumbnail")
    void updateProfileTest() {
        // given
        String newNickname = "newNickname";
        String newThumbnail = "newThumbnail.jpg";

        // when
        userService.updateProfile(user, newNickname, newThumbnail);

        // then
        assertEquals(newNickname, user.getNickname());
        assertEquals(newThumbnail, user.getThumbnail());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("알림 설정 변경 - 데이트 알림 ON, 이벤트 알림 OFF")
    void updateNotificationSettingsTest() {
        // given
        Boolean dateNotificationEnabled = true;
        Boolean eventNotificationEnabled = false;
        NotificationSettingsDto notificationSettingsDto = new NotificationSettingsDto(dateNotificationEnabled, eventNotificationEnabled);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // when
        userService.updateNotificationSettings(user, notificationSettingsDto);


        // then
        assertAll(
            () -> assertEquals(dateNotificationEnabled, user.getDateNotificationEnabled()),
            () -> assertEquals(eventNotificationEnabled, user.getEventNotificationEnabled())
        );
        verify(userRepository).save(user);
    }
}
