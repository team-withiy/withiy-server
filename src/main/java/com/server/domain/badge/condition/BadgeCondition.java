package com.server.domain.badge.condition;

import com.server.domain.badge.entity.BadgeType;
import com.server.domain.user.entity.User;

public interface BadgeCondition {

	BadgeType getBadgeType(); // 조건이 어떤 배지 타입에 해당하는지

	boolean isSatisfied(User user); // 조건을 만족하는지 여부
}
