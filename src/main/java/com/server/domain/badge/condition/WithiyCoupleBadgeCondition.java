package com.server.domain.badge.condition;

import com.server.domain.badge.entity.BadgeType;
import com.server.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class WithiyCoupleBadgeCondition implements BadgeCondition{
    @Override
    public BadgeType getBadgeType() {
        return BadgeType.WITHIY_COUPLE;
    }

    @Override
    public boolean isSatisfied(User user) {
        return user.getCouple() != null; // 커플 연결되어 있으면 true
    }
}
