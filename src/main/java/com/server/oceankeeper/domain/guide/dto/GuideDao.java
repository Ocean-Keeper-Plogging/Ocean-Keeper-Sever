package com.server.oceankeeper.domain.guide.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuideDao {
    private final Long id;
    private final String title;
    private final String videoName;
    private final String videoLink;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
}
