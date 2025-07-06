package com.server.domain.badge.repository;

import com.server.domain.badge.entity.Badge;
import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.UserBadge;
import com.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    boolean existsByUserAndBadge(User user, Badge badge);

    Optional<UserBadge> findByUserAndIsMainTrue(User user);

    Optional<UserBadge> findByUserAndBadgeType(User user, BadgeType badgeType);
}
