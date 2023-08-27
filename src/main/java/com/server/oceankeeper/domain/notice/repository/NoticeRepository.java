package com.server.oceankeeper.domain.notice.repository;

import com.server.oceankeeper.domain.notice.entity.Notice;
import org.springframework.data.repository.CrudRepository;

public interface NoticeRepository extends CrudRepository<Notice, Long>,NoticeQueryDslRepository {
}
