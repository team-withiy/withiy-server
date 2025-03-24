package com.server.domain.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.oauth.entity.OAuth;
import com.server.domain.oauth.enums.SocialType;

@Repository
public interface OAuthRepository extends JpaRepository<OAuth, String> {

    Optional<OAuth> findBySocialTypeAndAuthId(SocialType socialType, String authId);
}
