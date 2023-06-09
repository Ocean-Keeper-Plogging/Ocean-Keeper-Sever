package com.server.oceankeeper.Domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByNickname(String nickname);

    public Optional<User> findByEmail(String email);

    public Optional<User> findById(Long id);

    public Optional<User> findByProviderAndProviderId(String provider, String providerId);

    public Optional<User> findByDeviceToken(String deviceToken);

    boolean existsByProviderAndProviderId(String provider, String providerId);

    boolean existsByNickname(String nickname);

    boolean existsByDeviceToken(String deviceToken);

}
