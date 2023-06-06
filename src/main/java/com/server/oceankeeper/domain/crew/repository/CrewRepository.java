package com.server.oceankeeper.domain.crew.repository;

import com.server.oceankeeper.domain.crew.entitiy.Crews;
import org.springframework.data.repository.CrudRepository;

public interface CrewRepository extends CrudRepository<Crews,Long>, CrewQuerydslRepository {
}
