package com.server.oceankeeper.domain.activity.repository;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityDetailRepository extends JpaRepository<ActivityDetail, Long> {
    Optional<ActivityDetail> findByActivity(Activity a);
}
