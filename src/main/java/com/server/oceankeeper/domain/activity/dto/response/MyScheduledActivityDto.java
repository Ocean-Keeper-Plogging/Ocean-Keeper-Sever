package com.server.oceankeeper.domain.activity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@ToString
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class MyScheduledActivityDto {
    @ApiModelProperty(
            value = "활동 고유 아이디",
            example = "61e2cd48008d11eebe560242ac120002"
    )
    private final String id;

    @ApiModelProperty(
            value = "활동 남은 날짜",
            example = "11"
    )
    private final Integer dDay;

    @ApiModelProperty(
            value = "활동 타이틀",
            example = "금능해변 플로깅 프로젝트"
    )
    private final String title;

    @ApiModelProperty(
            value = "활동 시작 시각",
            example = "03.20(월) 13시 시작"
    )
    private final String startDay;

    @ApiModelProperty(
            value = "활동 지역",
            example = "제주도 능금해변"
    )
    private final String location;
}
