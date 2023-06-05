package com.server.oceankeeper.domain.activity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyActivityDto {
    @ApiModelProperty(
            value = "활동 고유 아이디",
            example = "61e2cd48008d11eebe560242ac120002"
    )
    private String id;

    @ApiModelProperty(
            value = "활동 남은 날짜",
            example = "11"
    )
    private Integer dDay;

    @ApiModelProperty(
            value = "활동 타이틀",
            example = "금능해변 플로깅 프로젝트"
    )
    private String title;

    @ApiModelProperty(
            value = "활동 시작 시각",
            example = "2023-03-20 12:00:00"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDay;

    @ApiModelProperty(
            value = "활동 지역",
            example = "제주도 능금해변"
    )
    private String location;
}
