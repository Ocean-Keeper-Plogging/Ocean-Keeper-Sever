package com.server.oceankeeper.domain.notice.repository;

import com.server.oceankeeper.domain.notice.dto.NoticeDao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NoticeQueryDslRepository {
    Slice<NoticeDao> getNotices(Long noticeId, Pageable pageable);
}
