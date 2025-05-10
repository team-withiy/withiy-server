package com.server.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.user.entity.Couple;
import com.server.domain.user.entity.User;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, Long> {

    Optional<Couple> findByUser1OrUser2(User user1, User user2);

    boolean existsByUser1OrUser2(User user1, User user2);
}
