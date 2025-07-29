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

import com.server.domain.oauth.entity.OAuth;
import com.server.domain.oauth.repository.OAuthRepository;
import com.server.domain.user.dto.NotificationSettingRequestDto;
import com.server.domain.user.dto.UserNotificationSettingResponseDto;
import com.server.domain.user.entity.Couple;
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
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TermAgreementRepository termAgreementRepository;

    @Mock
    private OAuthRepository oauthRepository;

    @Mock
    private CoupleService coupleService;

    @InjectMocks
    private UserService userService;

    private User user;
    private OAuth oauth;
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

        // Setup OAuth object
        oauth = OAuth.builder()
            .provider("google")
            .providerId("1234567890")
            .thumbnail("https://google.com")
            .nickname("testUser")
            .email("email@email.com")
            .build();

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
        // Given
        // 유저와 파트너 설정
        User partner = new User();
        partner.setId(2L);
        partner.setNickname("partnerUser");
        partner.setThumbnail("partner-thumbnail.jpg");

        user.setId(1L); // 현재 유저 ID 설정

        Couple mockCouple = new Couple();
        mockCouple.setId(10L);
        mockCouple.setUser1(user);     // 현재 유저
        mockCouple.setUser2(partner);  // 상대 유저
        mockCouple.setDeletedAt(null);

        // Setup all required terms as agreed
        for (TermAgreement agreement : user.getTermAgreements()) {
            agreement.setAgreed(true);
        }

        when(coupleService.getCoupleOrNull(any())).thenReturn(mockCouple);
        // Call the method
        UserDto userDto = userService.getUser(user);

        // Verify the results
        assertNotNull(userDto);
        assertEquals("testUser", userDto.getNickname());
        assertEquals("thumbnail.jpg", userDto.getThumbnail());
        assertEquals("USER123", userDto.getCode());
        assertFalse(userDto.getRestoreEnabled());
        assertTrue(userDto.getIsRegistered());
        assertTrue(userDto.getHasCouple());
        assertNotNull(userDto.getCouple());
        assertNull(userDto.getRestorableCouple());
        assertFalse(userDto.getHasRestorableCouple());
    }

    @Test
    @DisplayName("Get user with no couple test")
    void getUserWithNoCoupleTest() {
        // Setup all required terms as agreed
        for (TermAgreement agreement : user.getTermAgreements()) {
            agreement.setAgreed(true);
        }

        // Simulate couple being disconnected
        when(coupleService.getCoupleOrNull(any())).thenReturn(null);

        // Call the method
        UserDto userDto = userService.getUser(user);

        // Verify the results
        assertNotNull(userDto);
        assertEquals("testUser", userDto.getNickname());
        assertEquals("thumbnail.jpg", userDto.getThumbnail());
        assertEquals("USER123", userDto.getCode());
        assertFalse(userDto.getRestoreEnabled());
        assertTrue(userDto.getIsRegistered());
        assertFalse(userDto.getHasCouple());
        assertNull(userDto.getCouple());
        assertFalse(userDto.getHasRestorableCouple());
        assertNull(userDto.getRestorableCouple());
    }

    @Test
    @DisplayName("Get user with restorable couple test")
    void getUserWithRestorableCoupleTest() {
        // Given
        // 유저와 파트너 설정
        User partner = new User();
        partner.setId(2L);
        partner.setNickname("partnerUser");
        partner.setThumbnail("partner-thumbnail.jpg");

        user.setId(1L); // 현재 유저 ID 설정

        Couple mockCouple = new Couple();
        mockCouple.setId(10L);
        mockCouple.setUser1(user);     // 현재 유저
        mockCouple.setUser2(partner);  // 상대 유저
        mockCouple.setDeletedAt(LocalDateTime.now().minusDays(10));

        // Setup all required terms as agreed
        for (TermAgreement agreement : user.getTermAgreements()) {
            agreement.setAgreed(true);
        }
        when(coupleService.getCoupleOrNull(any())).thenReturn(mockCouple);
        // Call the method
        UserDto userDto = userService.getUser(user);

        assertNotNull(userDto);
        assertEquals("testUser", userDto.getNickname());
        assertEquals("thumbnail.jpg", userDto.getThumbnail());
        assertEquals("USER123", userDto.getCode());
        assertFalse(userDto.getRestoreEnabled());
        assertTrue(userDto.getIsRegistered());
        assertFalse(userDto.getHasCouple());
        assertNull(userDto.getCouple());
        assertTrue(userDto.getHasRestorableCouple());
        assertNotNull(userDto.getRestorableCouple());
    }

    @Test
    @DisplayName("Get user with unrestorable couple test")
    void getUserWithUnrestorableCoupleTest() {
        // Given
        // 유저와 파트너 설정
        User partner = new User();
        partner.setId(2L);
        partner.setNickname("partnerUser");
        partner.setThumbnail("partner-thumbnail.jpg");

        user.setId(1L); // 현재 유저 ID 설정

        Couple mockCouple = new Couple();
        mockCouple.setId(10L);
        mockCouple.setUser1(user);     // 현재 유저
        mockCouple.setUser2(partner);  // 상대 유저
        mockCouple.setDeletedAt(LocalDateTime.now().minusDays(31)); // 31일 이상 지난 커플

        // Setup all required terms as agreed
        for (TermAgreement agreement : user.getTermAgreements()) {
            agreement.setAgreed(true);
        }
        when(coupleService.getCoupleOrNull(any())).thenReturn(mockCouple);
        // Call the method
        UserDto userDto = userService.getUser(user);

        assertNotNull(userDto);
        assertEquals("testUser", userDto.getNickname());
        assertEquals("thumbnail.jpg", userDto.getThumbnail());
        assertEquals("USER123", userDto.getCode());
        assertFalse(userDto.getRestoreEnabled());
        assertTrue(userDto.getIsRegistered());
        assertFalse(userDto.getHasCouple());
        assertNull(userDto.getCouple());
        assertFalse(userDto.getHasRestorableCouple());
        assertNull(userDto.getRestorableCouple());
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
        when(oauthRepository.findByUser(any())).thenReturn(Optional.of(oauth));

        // Call the method with forAccountWithdrawal = false
        String result = userService.deleteUser(user, false);
        // Verify the results
        assertEquals("testUser", result); // Should return original nickname
        assertNull(user.getDeletedAt()); // deletedAt should be null
        assertEquals("https://google.com", user.getThumbnail()); // Thumbnail should be cleared
        assertEquals("testUser", user.getNickname());
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
    @DisplayName("Set new profile test - set only thumbnail")
    void updateProfileWithOnlyThumbnailTest() {
        // given
        String newThumbnail = "newThumbnail.jpg";

        // when
        userService.updateProfile(user, null, newThumbnail);

        // then
        assertEquals("testUser", user.getNickname()); // Nickname should remain unchanged
        assertEquals(newThumbnail, user.getThumbnail());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Set new profile test - set only nickname")
    void updateProfileWithOnlyNicknameTest() {
        // given
        String newNickname = "newNickname";

        // when
        userService.updateProfile(user, newNickname, null);

        // then
        assertEquals(newNickname, user.getNickname());
        assertEquals("thumbnail.jpg", user.getThumbnail()); // Thumbnail should remain unchanged
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("알림 설정 변경 - 데이트 알림 ON, 이벤트 알림 OFF")
    void updateNotificationSettingsTest() {
        // given
        Boolean dateNotificationEnabled = true;
        Boolean eventNotificationEnabled = false;
        NotificationSettingRequestDto notificationSettingsDto = new NotificationSettingRequestDto(dateNotificationEnabled, eventNotificationEnabled);

        // when
        userService.updateNotificationSettings(user, notificationSettingsDto);

        // then
        assertAll(
            () -> assertEquals(dateNotificationEnabled, user.getDateNotificationEnabled()),
            () -> assertEquals(eventNotificationEnabled, user.getEventNotificationEnabled())
        );
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("알림 설정 변경 - 데이트 알림 ON, 이벤트 알림 NULL")
    void updateNotificationSettingsNullTest() {
        // given
        Boolean dateNotificationEnabled = true;
        NotificationSettingRequestDto notificationSettingsDto = new NotificationSettingRequestDto(dateNotificationEnabled, null);

        // when
        userService.updateNotificationSettings(user, notificationSettingsDto);

        // then
        assertAll(
            () -> assertEquals(dateNotificationEnabled, user.getDateNotificationEnabled()),
            () -> assertTrue(user.getEventNotificationEnabled()) // Default value should be true
        );
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("알림 설정 조회 - 데이트 알림 ON, 이벤트 알림 OFF")
    void getNotificationSettingsTest() {
        // given
        user.setDateNotificationEnabled(true);
        user.setEventNotificationEnabled(false);

        // when
        UserNotificationSettingResponseDto responseDto = userService.getNotificationSettings(user);

        // then
        assertAll(
            () -> assertEquals(user.getId(), responseDto.getUserId()),
            () -> assertTrue(responseDto.getDateNotificationEnabled()),
            () -> assertFalse(responseDto.getEventNotificationEnabled())
        );
    }
}
