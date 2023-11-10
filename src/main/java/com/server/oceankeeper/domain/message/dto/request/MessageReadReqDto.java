package com.server.oceankeeper.domain.message.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MessageReadReqDto {
    @ApiModelProperty(value = "읽음 확인 변경할 메세지 아이디", example = "19", required = true)
    private final Long messageId;
    @ApiModelProperty(value = "읽음 확인", example = "true", required = true)
    private final boolean read;
}
