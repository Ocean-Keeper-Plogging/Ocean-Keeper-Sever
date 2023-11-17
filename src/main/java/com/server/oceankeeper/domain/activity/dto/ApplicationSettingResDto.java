package com.server.oceankeeper.domain.activity.dto;

import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ApplicationSettingResDto {
    @ApiModelProperty(
            value="api 실행 결과 true/false",
            example = "true",
            required = true
    )
    private final boolean result;
    @ApiModelProperty(
            value = "바뀐 crew status NO_SHOW/REJECT/IN_PROGRESS만 가능. 한번 REJECT으로 설정하면, 더이상 변경 불가.",
            example = "NO_SHOW/REJECT",
            required = true
    )
    private final CrewStatus crewStatus;

    @ApiModelProperty(
            value = "거절메세지 아이디. REJECT으로 하였을때만 존재함. 나머지는 null",
            example = "[13,14,15]"
    )
    private List<Long> messageId;

    public ApplicationSettingResDto(boolean result, CrewStatus crewStatus, List<Long> messageId) {
        this.result = result;
        this.crewStatus = crewStatus;
        this.messageId = messageId;
    }
}
