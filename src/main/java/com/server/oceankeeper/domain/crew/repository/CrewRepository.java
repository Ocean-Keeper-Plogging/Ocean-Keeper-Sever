package com.server.oceankeeper.domain.crew.repository;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CrewRepository extends CrudRepository<Crews,Long>, CrewQuerydslRepository {
    List<Crews> findByActivity(Activity activity);
    Optional<Crews> findTopByUserOrderByCreatedAtDesc(OUser user);
    Optional<Crews> findByActivityAndActivityRole(Activity activity, CrewRole activityRole);

    Optional<Crews> findByUserAndActivity(OUser user, Activity activity);
}
