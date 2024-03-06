package com.server.oceankeeper.domain.guide.repository;

import com.server.oceankeeper.domain.guide.entity.Guide;
import org.springframework.data.repository.CrudRepository;

public interface GuideRepository extends CrudRepository<Guide, Long>, GuideQueryDslRepository {
}
