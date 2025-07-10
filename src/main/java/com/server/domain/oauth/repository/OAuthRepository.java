package com.server.domain.oauth.repository;

import java.util.Optional;

import com.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.oauth.entity.OAuth;

@Repository
public interface OAuthRepository extends JpaRepository<OAuth, Long> {

    Optional<OAuth> findByProviderAndProviderId(String provider, String providerId);
    Optional<OAuth> findByUser(User user);

}
