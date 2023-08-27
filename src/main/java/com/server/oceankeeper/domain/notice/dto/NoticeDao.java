package com.server.oceankeeper.domain.notice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeDao {
    private final Long noticeId;
    private final String title;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
}
