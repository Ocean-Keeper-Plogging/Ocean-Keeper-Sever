package com.server.oceankeeper.domain.guide.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GuideModifyReqDto {
    @NotEmpty
    private final Long id;
    private final String videoName;
    private final String videoLink;
    private final String title;
}
