package com.server.domain.badge.repository;

import com.server.domain.badge.entity.Badge;
import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.UserBadge;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

	boolean existsByUserAndBadge(User user, Badge badge);

	Optional<UserBadge> findByUserAndIsMainTrue(User user);

	Optional<UserBadge> findByUserAndBadgeType(User user, BadgeType badgeType);

	List<UserBadge> findByUser(User user);
}
