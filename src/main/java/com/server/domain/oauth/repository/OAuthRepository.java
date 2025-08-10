package com.server.domain.oauth.repository;

import com.server.domain.oauth.entity.OAuth;
import com.server.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthRepository extends JpaRepository<OAuth, Long> {

	Optional<OAuth> findByProviderAndProviderId(String provider, String providerId);

	Optional<OAuth> findByUser(User user);

}
