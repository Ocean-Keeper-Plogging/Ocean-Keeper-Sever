package com.server.oceankeeper.domain.user.repository;

import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.domain.user.entity.UserRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<OUser, Long> {
    Optional<OUser> findByNicknameAndPassword(String nickname, String password);
    Slice<OUser> findAllByAlarmAndRole(boolean alarm, UserRole role, Pageable pageable);
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
