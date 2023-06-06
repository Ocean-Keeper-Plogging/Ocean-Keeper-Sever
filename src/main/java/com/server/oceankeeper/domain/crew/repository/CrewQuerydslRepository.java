package com.server.oceankeeper.domain.crew.repository;

import com.server.oceankeeper.domain.activity.dto.response.MyActivityDao;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;

import java.util.List;

public interface CrewQuerydslRepository {
    List<MyActivityDao> getMyActivities(MyActivityParam myActivityParam);
}