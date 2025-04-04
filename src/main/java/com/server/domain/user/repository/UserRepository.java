package com.server.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findById(Long id);

    Optional<User> findByNickname(String nickname);


}
