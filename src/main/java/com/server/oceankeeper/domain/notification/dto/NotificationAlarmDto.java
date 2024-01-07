package com.server.oceankeeper.domain.notification.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NotificationAlarmDto {
    @ApiModelProperty(value = "알람 설정 여부")
    private final boolean alarm;
}
