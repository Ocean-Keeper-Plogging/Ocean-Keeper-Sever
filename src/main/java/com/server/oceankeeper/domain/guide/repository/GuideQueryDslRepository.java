package com.server.oceankeeper.domain.guide.repository;

import com.server.oceankeeper.domain.guide.dto.GuideDao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GuideQueryDslRepository {
    Slice<GuideDao> getData(Long noticeId, Pageable pageable);
}
