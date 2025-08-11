package com.server.domain.badge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.domain.badge.condition.BadgeCondition;
import com.server.domain.badge.condition.BadgeConditionFactory;
import com.server.domain.badge.dto.BadgeResponseDto;
import com.server.domain.badge.entity.Badge;
import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.CharacterType;
import com.server.domain.badge.entity.UserBadge;
import com.server.domain.badge.repository.BadgeRepository;
import com.server.domain.badge.repository.UserBadgeRepository;
import com.server.domain.term.entity.Term;
import com.server.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

public class BadgeServiceTest {

	@Mock
	private BadgeRepository badgeRepository;

	@Mock
	private UserBadgeRepository userBadgeRepository;

	@Mock
	private BadgeConditionFactory badgeConditionFactory;

	@Mock
	private BadgeCondition badgeCondition;

	@InjectMocks
	private BadgeService badgeService;

	private User user;
	private List<Term> terms;
	private Badge badge1;
	private Badge badge2;
	private List<Badge> badges;

	@BeforeEach
	void setUp() {

		// Setup Term objects using constructors instead of setters
		terms = new ArrayList<>();
		Term requiredTerm = new Term(1L, "Required Term", "Required term content", true);
		Term optionalTerm = new Term(2L, "Optional Term", "Optional term content", false);

		terms.add(requiredTerm);
		terms.add(optionalTerm);
		// Setup User
		user = User.builder()
			.nickname("testUser")
			.thumbnail("thumbnail.jpg")
			.terms(terms)
			.code("USER123")
			.build();
		user.setId(1L);

		// Setup Badges
		badges = new ArrayList<>();
		badge1 = Badge.builder()
			.type(BadgeType.WITHIY_MEMORY)
			.build();
		badge1.setId(2L);
		badges.add(badge1);
		badge2 = Badge.builder()
			.type(BadgeType.WITHIY_REGULAR)
			.build();
		badge2.setId(3L);
		badges.add(badge2);
	}

	@Test
	@DisplayName("✅ 배지 조회 성공 테스트")
	void getBadgeListSuccess() {
		// Setup
		when(badgeRepository.findAll())
			.thenReturn(badges);

		when(userBadgeRepository.findByUser(user))
			.thenReturn(
				List.of(
					UserBadge.builder().user(user).badge(badge1).isMain(true).build(),
					UserBadge.builder().user(user).badge(badge2).isMain(false).build()
				)
			);
		// Call the method
		List<BadgeResponseDto> results = badgeService.getBadgeListWithUserInfo(user);

		// Verify
		assertEquals(2, results.size());
		assertEquals(results.get(0).getBadgeName(), BadgeType.WITHIY_MEMORY.getDisplayName());
		assertEquals(results.get(0).getBadgeDescription(),
			BadgeType.WITHIY_MEMORY.getDescription());
		assertEquals(results.get(1).getBadgeName(), BadgeType.WITHIY_REGULAR.getDisplayName());
		assertTrue(results.get(0).isMain());
		assertFalse(results.get(1).isMain());
	}

	@Test
	@DisplayName("✅ 메인 배지 업데이트 성공 테스트")
	void updateMainBadgeSuccess() {
		// Setup
		UserBadge currentMainBadge = UserBadge.builder()
			.user(user)
			.badge(badge1)
			.isMain(true)
			.build();

		UserBadge updatedMainBadge = UserBadge.builder()
			.user(user)
			.badge(badge2)
			.isMain(false)
			.build();

		when(userBadgeRepository.findByUserAndIsMainTrue(user))
			.thenReturn(Optional.of(currentMainBadge));

		when(userBadgeRepository.findByUserAndBadgeType(user, BadgeType.WITHIY_REGULAR))
			.thenReturn(Optional.of(updatedMainBadge));

		// Call the method
		badgeService.updateMainBadge(user, BadgeType.WITHIY_REGULAR, CharacterType.RABBIT);

		// Verify
		assertEquals(BadgeType.WITHIY_REGULAR, updatedMainBadge.getBadge().getType());
		assertTrue(updatedMainBadge.getIsMain());
	}

	@Test
	@DisplayName("✅ 배지 획득 성공 테스트")
	void claimBadgeSuccess() {
		// given
		Badge badge = Badge.builder()
			.type(BadgeType.WITHIY_COUPLE)
			.build();
		CharacterType characterType = CharacterType.RABBIT;

		// Setup
		when(badgeRepository.findByType(badge.getType()))
			.thenReturn(Optional.of(badge));

		when(userBadgeRepository.existsByUserAndBadge(user, badge))
			.thenReturn(false);

		when(badgeConditionFactory.getCondition(badge.getType()))
			.thenReturn(Optional.of(badgeCondition));

		when(badgeCondition.isSatisfied(user))
			.thenReturn(true);

		// Call the method
		badgeService.claimBadge(user, badge.getType(), characterType);

		// Verify
		ArgumentCaptor<UserBadge> captor = ArgumentCaptor.forClass(UserBadge.class);
		verify(userBadgeRepository).save(captor.capture());

		UserBadge savedBadge = captor.getValue();
		assertEquals(user, savedBadge.getUser());
		assertEquals(badge, savedBadge.getBadge());
		assertEquals(characterType, savedBadge.getCharacterType());
		assertFalse(savedBadge.getIsMain());
	}
}
