package com.server.domain.badge.condition;

import com.server.domain.badge.entity.BadgeType;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.CoupleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WithiyCoupleBadgeCondition implements BadgeCondition {

	private final CoupleService coupleService;

	@Override
	public BadgeType getBadgeType() {
		return BadgeType.WITHIY_COUPLE;
	}

	@Override
	public boolean isSatisfied(User user) {
		return coupleService.isUserInCouple(user);
	}
}
