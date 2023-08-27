package com.server.oceankeeper.domain.notice.dto.request;

import lombok.Data;

@Data
public class NoticeModifyReqDto {
    private final Long id;
    private final String contents;
    private final String title;
}
