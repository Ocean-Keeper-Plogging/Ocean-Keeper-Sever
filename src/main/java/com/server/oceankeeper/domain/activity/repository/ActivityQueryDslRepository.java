package com.server.oceankeeper.domain.activity.repository;

import com.server.oceankeeper.domain.activity.dto.ActivityDao;
import com.server.oceankeeper.domain.activity.dto.MyActivityDao;
import com.server.oceankeeper.domain.activity.dto.AllActivityDao;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

public interface ActivityQueryDslRepository {
    Slice<AllActivityDao> getAllActivities(UUID activityId, ActivityStatus status, LocationTag tag, GarbageCategory category, Pageable pageable);

    Slice<ActivityDao> getMyActivities(UUID userId, UUID activityId, ActivityStatus activityStatus, CrewRole crewRole, Pageable pageable);

    List<MyActivityDao> getMyActivitiesLimit5(MyActivityParam myActivityParam);
}
