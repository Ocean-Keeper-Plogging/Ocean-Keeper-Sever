package com.server.oceankeeper.domain.crew.repository;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrewRepository extends CrudRepository<Crews, Long> {
    List<Crews> findByActivity(Activity activity);

    List<Crews> findByUser(OUser user);

    Optional<Crews> findByUserAndUuid(OUser user, UUID uuid);

    Optional<Crews> findCrewsByUserOrderByCreatedAtDesc(OUser user);

    Optional<Crews> findByActivityAndActivityRole(Activity activity, CrewRole activityRole);

    Optional<Crews> findByUuidAndActivityRole(UUID uuid, CrewRole activityRole);

    Optional<Crews> findByUserAndActivity(OUser user, Activity activity);

    Optional<Crews> findByUuid(UUID uuid);
}
