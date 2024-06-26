package com.server.oceankeeper.domain.activity.repository;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.user.entity.OUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, Long>, ActivityQueryDslRepository {
    Optional<Activity> findByUuid(UUID uuid);

    List<Activity> findByHost(OUser host);
}
