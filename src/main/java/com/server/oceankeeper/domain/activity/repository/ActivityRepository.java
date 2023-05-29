package com.server.oceankeeper.domain.activity.repository;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    //List<Activity> findByUserOrderAndStartAtAsc(OUser user);

    Page<?> findByActivityStatusAndLocation(ActivityStatus activityStatus,
                                            LocationTag locationTag, Pageable pageable);

    Page<?> findByActivityStatusAndGarbageCategory(ActivityStatus activityStatus,
                                                   GarbageCategory garbageCategory,
                                                   Pageable pageable);

    Page<?> findByLocation(LocationTag locationTag, Pageable pageable);

    Page<?> findByGarbageCategory(GarbageCategory garbageCategory, Pageable pageable);

    //@Query("select u from User u where u.user_id=:id and u.status="OPEN" order by ")
    //List<Activity> findByUserIdAndActivityStatusStartAtAsc(Long id, ActivityStatus status);
}
