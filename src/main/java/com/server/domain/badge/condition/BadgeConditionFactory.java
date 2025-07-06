package com.server.domain.badge.condition;

import com.server.domain.badge.entity.BadgeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BadgeConditionFactory {
    private final Map<BadgeType, BadgeCondition> conditionMap;

    public BadgeConditionFactory(List<BadgeCondition> conditions) {
        this.conditionMap = conditions.stream()
            .collect(Collectors.toMap(BadgeCondition::getBadgeType, Function.identity()));
    }

    public Optional<BadgeCondition> getCondition(BadgeType badgeType) {
        return Optional.ofNullable(conditionMap.get(badgeType));
    }
}
