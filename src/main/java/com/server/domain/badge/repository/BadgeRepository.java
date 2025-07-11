package com.server.domain.badge.repository;

import com.server.domain.badge.entity.Badge;
import com.server.domain.badge.entity.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByType(BadgeType type);
}
