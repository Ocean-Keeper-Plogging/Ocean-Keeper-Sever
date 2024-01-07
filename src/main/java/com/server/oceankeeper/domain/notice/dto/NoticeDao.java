package com.server.oceankeeper.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeDao {
    private final Long id;
    private final String title;
    private final String contents;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm", timezone = "Asia/Seoul")
    private final LocalDateTime modifiedAt;
}
