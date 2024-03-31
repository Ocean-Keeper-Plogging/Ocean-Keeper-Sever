package com.server.oceankeeper.domain.activity.repository;

import com.server.oceankeeper.domain.activity.dao.*;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.user.entity.OUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ActivityQueryDslRepository {
    Slice<AllActivityDao> getAllActivities(UUID activityId, ActivityStatus status, LocationTag tag,
                                           GarbageCategory category, Pageable pageable, OUser userParam);

    Slice<ActivityDao> getMyActivitiesWithoutCancel(UUID userId, UUID activityId, ActivityStatus activityStatus, CrewRole crewRole, LocalDateTime startAt, Pageable pageable);

    List<MyActivityDao> getMyActivitiesLimit5(MyActivityParam myActivityParam);

    List<HostActivityDao> getHostActivityNameFromUser(OUser user); //Get only in-progress activity

    List<CrewInfoDao> getCrewInfoFromHostUser(OUser user, UUID activityId); //Get only in-progress activity

    List<CrewInfoDetailDao> getCrewInfo(UUID activityId);

    List<CrewDeviceTokensDao> getUserFromActivityId(UUID activityId, CrewRole crewRole);

    long selectByCrewStatusAndStartAtAndUpdateCrewStatusAsDeleted(CrewStatus status, long days);
}
