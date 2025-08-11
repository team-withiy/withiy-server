package com.server.domain.badge.repository;

import com.server.domain.badge.entity.Badge;
import com.server.domain.badge.entity.BadgeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

	Optional<Badge> findByType(BadgeType type);
}
