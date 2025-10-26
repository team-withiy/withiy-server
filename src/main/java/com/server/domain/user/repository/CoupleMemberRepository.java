package com.server.domain.user.repository;

import com.server.domain.user.entity.CoupleMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleMemberRepository extends JpaRepository<CoupleMember, Long> {

	Optional<CoupleMember> findByUserId(Long userId);

	Optional<CoupleMember> findByCoupleIdAndUserIdNot(Long coupleId, Long userId);

	void deleteAllByCoupleId(Long coupleId);

}
