package com.server.oceankeeper.domain.crew.repository;

import com.server.oceankeeper.domain.activity.dao.FullApplicationDao;

import java.util.List;
import java.util.UUID;

public interface CrewQueryDslRepository {
    List<FullApplicationDao> getApplicationAndActivityInfoAndCrewInfo(UUID applicationId);
}
