package com.server.oceankeeper.domain.statistics.repository;

import com.server.oceankeeper.domain.statistics.entity.ActivityInfo;
import com.server.oceankeeper.domain.user.entity.OUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ActivityInfoRepository extends CrudRepository<ActivityInfo, Long> {
    Optional<ActivityInfo> findByUser(OUser user);
}
