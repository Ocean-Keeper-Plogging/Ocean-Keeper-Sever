package com.server.oceankeeper.domain.guide.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GuideModifyReqDto {
    @NotEmpty
    private final Long id;
    private final String contents;
    private final String title;
}
