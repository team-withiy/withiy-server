package com.server.domain.badge.service;

import com.server.domain.badge.condition.BadgeCondition;
import com.server.domain.badge.condition.BadgeConditionFactory;
import com.server.domain.badge.dto.BadgeResponseDto;
import com.server.domain.badge.entity.Badge;
import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.CharacterType;
import com.server.domain.badge.entity.UserBadge;
import com.server.domain.badge.repository.BadgeRepository;
import com.server.domain.badge.repository.UserBadgeRepository;
import com.server.domain.user.entity.User;
import com.server.global.error.code.BadgeErrorCode;
import com.server.global.error.exception.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {

	private final BadgeRepository badgeRepository;
	private final UserBadgeRepository userBadgeRepository;
	private final BadgeConditionFactory conditionFactory;

	@Transactional
	public void claimBadge(User user, BadgeType badgeType, CharacterType characterType) {

		// 1. 배지 유효성 검사 로직을 구현합니다.
		Badge badge = badgeRepository.findByType(badgeType)
			.orElseThrow(() -> new BusinessException(BadgeErrorCode.BADGE_NOT_FOUND));

		// 2. 유저가 이미 배지를 획득했는지 확인합니다.
		boolean alreadyClaimed = userBadgeRepository.existsByUserAndBadge(user, badge);
		if (alreadyClaimed) {
			throw new BusinessException(BadgeErrorCode.BADGE_ALREADY_EXISTS);
		}

		// 3. 유저 배지 획득 조건을 확인하고, 해당 배지를 획득할 수 있는지 검증합니다.
		BadgeCondition condition = conditionFactory.getCondition(badgeType)
			.orElseThrow(() -> new BusinessException(BadgeErrorCode.BADGE_CONDITION_NOT_MET));

		boolean isQualified = condition.isSatisfied(user);

		// 4. 배지 획득 로직을 구현합니다. 획득한 배지의 메인 배지 여부는 false로 설정합니다.
		if (isQualified) {
			UserBadge userBadge = UserBadge.builder()
				.user(user)
				.badge(badge)
				.characterType(characterType)
				.isMain(false)
				.build();

			userBadgeRepository.save(userBadge);
		}
	}

	@Transactional
	public void updateMainBadge(User user, BadgeType badgeType, CharacterType characterType) {

		// 1. 기존 메인 배지를 찾아서 메인 배지 설정을 해제합니다.
		userBadgeRepository.findByUserAndIsMainTrue(user)
			.ifPresent(mainBadge -> {
				mainBadge.updateMain(false);
				userBadgeRepository.save(mainBadge);
			});

		// 2. 유저가 보유한 배지인지 확인합니다.
		UserBadge newMainUserBadge = userBadgeRepository.findByUserAndBadgeType(user, badgeType)
			.orElseThrow(() -> new BusinessException(BadgeErrorCode.BADGE_NOT_CLAIMED));

		// 3. 새로운 배지를 메인 배지로 설정합니다.
		newMainUserBadge.updateMain(true);
		newMainUserBadge.updateCharacterType(characterType);
	}

	@Transactional(readOnly = true)
	public List<BadgeResponseDto> getBadgeListWithUserInfo(User user) {
		// 1. 모든 배지 정보를 조회합니다.
		List<Badge> allBadges = badgeRepository.findAll();

		// 2. 유저가 보유한 배지 정보를 조회합니다.
		Map<BadgeType, UserBadge> ownedMap = userBadgeRepository.findByUser(user).stream()
			.collect(Collectors.toMap(
				ub -> ub.getBadge().getType(),
				Function.identity()
			));

		return allBadges.stream()
			.map(badge -> {
				UserBadge userBadge = ownedMap.get(badge.getType());
				boolean isOwned = userBadge != null;
				boolean isMain = isOwned && Boolean.TRUE.equals(userBadge.getIsMain());
				CharacterType characterType = isOwned ? userBadge.getCharacterType() : null;

				return BadgeResponseDto.builder()
					.badgeType(badge.getType())
					.badgeName(badge.getType().getDisplayName())
					.badgeDescription(badge.getType().getDescription())
					.item(badge.getType().getItem())
					.isOwned(isOwned)
					.isMain(isMain)
					.characterType(characterType)
					.build();
			})
			.collect(Collectors.toList());
	}
}
