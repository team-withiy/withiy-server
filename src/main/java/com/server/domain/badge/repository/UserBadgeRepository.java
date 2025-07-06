package com.server.domain.badge.repository;

import com.server.domain.badge.entity.Badge;
import com.server.domain.badge.entity.UserBadge;
import com.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    boolean existsByUserAndBadge(User user, Badge badge);
}
