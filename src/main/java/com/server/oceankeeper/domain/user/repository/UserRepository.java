package com.server.oceankeeper.domain.user.repository;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<OUser, Long> {
    Optional<OUser> findByUuid(UUID uuid);

    Optional<OUser> findByNickname(String nickname);

    Optional<OUser> findByEmail(String email);

    Optional<OUser> findByProviderAndProviderId(String provider, String providerId);

    Optional<OUser> findByDeviceToken(String deviceToken);

    boolean existsByProviderAndProviderId(String provider, String providerId);

    boolean existsByNickname(String nickname);

    boolean existsByDeviceToken(String deviceToken);

    boolean existsByUuid(UUID uuid);
}
