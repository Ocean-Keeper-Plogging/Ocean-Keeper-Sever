package com.server.oceankeeper.domain.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.oceankeeper.domain.notice.dto.NoticeDao;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeDto {
    private final Long id;
    private final String title;
    private final String contents;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm", timezone = "Asia/Seoul")
    private final LocalDateTime modifiedAt;

    public NoticeDto(NoticeDao noticeDao) {
        id = noticeDao.getId();
        title = noticeDao.getTitle();
        contents = noticeDao.getContents();
        createdAt = noticeDao.getCreatedAt();
        modifiedAt = noticeDao.getModifiedAt();
    }
    public NoticeDto(NoticeDao noticeDao, String contents) {
        this.id = noticeDao.getId();
        this.title = noticeDao.getTitle();
        this.contents = contents;
        this.createdAt = noticeDao.getCreatedAt();
        this.modifiedAt = noticeDao.getModifiedAt();
    }
}
