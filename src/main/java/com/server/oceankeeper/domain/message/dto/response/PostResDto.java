package com.server.oceankeeper.domain.message.dto.response;

import com.server.oceankeeper.domain.activity.dto.response.GetActivityResDto;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.message.entity.MessageType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResDto {
    private final List<MessageDto> messages;
    private final Meta meta;

    @Getter
    @RequiredArgsConstructor
    public static class MessageDto {
        private final Long id;
        private final MessageType type;

        private final String from;
        private final String activityId;
        private final String title;
        private final GarbageCategory garbageCategory;

        private final LocalDateTime time;

        private final boolean read;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Meta {
        @ApiModelProperty(
                value = "쪽지 개수",
                example = "5",
                required = true
        )
        private final Integer size;
        @ApiModelProperty(
                value = "남은 페이지가 있는지 여부. 있다면 messages 파라미터 중 마지막에 전달된 쪽지 id를 다음 쪽지 요청시 넣어줘야함.",
                example = "false",
                required = true
        )
        private final boolean isLast;
    }
}
