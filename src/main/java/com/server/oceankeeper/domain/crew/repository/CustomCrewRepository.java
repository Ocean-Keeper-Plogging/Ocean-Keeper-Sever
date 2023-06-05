package com.server.oceankeeper.domain.crew;

import com.server.oceankeeper.domain.activity.dto.response.MyActivityDao;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;

import java.util.List;

public interface CustomCrewRepository {
    List<MyActivityDao> getMyActivities(MyActivityParam myActivityParam);
}