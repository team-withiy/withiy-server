package com.server.domain.badge.service;

import com.server.domain.badge.condition.BadgeCondition;
import com.server.domain.badge.condition.BadgeConditionFactory;
import com.server.domain.badge.entity.Badge;
import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.CharacterType;
import com.server.domain.badge.entity.UserBadge;
import com.server.domain.badge.repository.BadgeRepository;
import com.server.domain.badge.repository.UserBadgeRepository;
import com.server.domain.user.entity.User;
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
        Badge badge  = badgeRepository.findByType(badgeType)
                .orElseThrow(() -> new IllegalArgumentException("배지를 찾을 수 없습니다."));

        // 2. 유저가 이미 배지를 획득했는지 확인합니다.
        boolean alreadyClaimed = userBadgeRepository.existsByUserAndBadge(user, badge);
        if( alreadyClaimed) {
            throw new IllegalArgumentException("이미 획득한 배지입니다: " + badgeType);
        }

        // 3. 유저 배지 획득 조건을 확인하고, 해당 배지를 획득할 수 있는지 검증합니다.
        BadgeCondition condition = conditionFactory.getCondition(badgeType)
                .orElseThrow(() -> new IllegalArgumentException("조건 검증 로직이 존재하지 않는 배지입니다: " + badgeType));

//        boolean isQualified = condition.isSatisfied(user);
        boolean isQualified = true; // TODO: 실제 조건 검증 로직을 구현해야 합니다.

        // 4. 배지 획득 로직을 구현합니다.
        if (isQualified) {
            UserBadge userBadge = UserBadge.builder()
                    .user(user)
                    .badge(badge)
                    .build();

            userBadgeRepository.save(userBadge);
        }
    }
}
