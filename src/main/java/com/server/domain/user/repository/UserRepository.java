package com.server.domain.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByCode(String code);

    // Find soft-deleted users whose deletedAt date is before the specified date
    List<User> findByDeletedAtNotNullAndDeletedAtBefore(LocalDateTime date);
}
