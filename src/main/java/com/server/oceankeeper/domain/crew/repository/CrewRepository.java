package com.server.oceankeeper.domain.crew.repository;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.entity.Crews;
import com.server.oceankeeper.domain.user.entity.OUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrewRepository extends CrudRepository<Crews, Long>,CrewQueryDslRepository {
    List<Crews> findByActivity(Activity activity);

    List<Crews> findByUser(OUser user);

    Optional<Crews> findByUserAndUuid(OUser user, UUID uuid);

    Optional<Crews> findFirstByUserAndNameIsNotNullOrderByIdDesc(OUser user);

    Optional<Crews> findByActivityAndActivityRole(Activity activity, CrewRole activityRole);
    Optional<Crews> findByIdAndActivityRole(Long activityId, CrewRole activityRole);

    Optional<Crews> findByUuidAndActivityRole(UUID uuid, CrewRole activityRole);

    Optional<Crews> findByUserAndActivity(OUser user, Activity activity);

    Optional<Crews> findByUuid(UUID uuid);

    List<Crews> findByUserAndCrewStatus(OUser user, CrewStatus status);
}
