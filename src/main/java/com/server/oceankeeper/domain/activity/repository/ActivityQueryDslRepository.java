package com.server.oceankeeper.domain.activity.repository;

import com.server.oceankeeper.domain.activity.dto.response.ActivityDao;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityQueryDslRepository {
    List<ActivityDao> findActivities(ActivityStatus status, LocationTag tag, GarbageCategory category, Pageable pageable);
}
