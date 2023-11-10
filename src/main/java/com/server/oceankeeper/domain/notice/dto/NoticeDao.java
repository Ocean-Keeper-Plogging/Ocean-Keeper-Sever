package com.server.oceankeeper.domain.notice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeDao {
    private final Long id;
    private final String title;
    private final String contents;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
}
