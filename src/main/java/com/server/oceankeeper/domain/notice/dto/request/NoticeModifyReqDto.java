package com.server.oceankeeper.domain.notice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class NoticeModifyReqDto {
    @NotEmpty
    private final Long id;
    private final String contents;
    private final String title;
}
