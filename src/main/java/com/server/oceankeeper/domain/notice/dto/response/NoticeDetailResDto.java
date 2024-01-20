package com.server.oceankeeper.domain.notice.dto.response;

import com.server.oceankeeper.domain.notice.entity.Notice;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NoticeDetailResDto {
    private final Long id;
    private final String title;
    private final String contents;
    private final LocalDate createdAt;
    private final LocalDate modifiedAt;

    public static NoticeDetailResDto fromEntity(Notice notice) {
        return new NoticeDetailResDto(
                notice.getId(),
                notice.getTitle(),
                notice.getContents(),
                notice.getCreatedAt().toLocalDate(),
                notice.getUpdatedAt().toLocalDate());
    }

    public static NoticeDetailResDto fromEntity(Notice notice, String contents) {
        return new NoticeDetailResDto(
                notice.getId(),
                notice.getTitle(),
                contents,
                notice.getCreatedAt().toLocalDate(),
                notice.getUpdatedAt().toLocalDate());
    }
}
