package com.server.oceankeeper.domain.user.repository;

import com.server.oceankeeper.domain.user.entitiy.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisRepository extends CrudRepository<RefreshToken, String> {
    //RefreshToken findByRefreshToken(String refreshToken);
    Optional<RefreshToken> findByKey(String key);
}
