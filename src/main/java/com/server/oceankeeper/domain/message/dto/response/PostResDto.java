package com.server.oceankeeper.domain.message.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @ApiModelProperty(
                value = "대상 메세지 아이디",
                notes = "Long 숫자",
                example = "15"
        )
        private final Long id;
        @ApiModelProperty(
                value = "메세지 타입",
                notes = "PRIVATE NOTICE REJECT 중 하나",
                example = "PRIVATE"
        )
        private final MessageType type;
        @ApiModelProperty(
                value = "메세지 보낸사람 활동 닉네임",
                notes = "메세지 보낸사람의 활동 닉네임이 표시됩니다.",
                example = "user1"
        )
        private final String from;
        @ApiModelProperty(
                value = "관련활동 아이디",
                notes = "메세지와 관련된 활동 아이디를 표시합니다.",
                example = "11ee3159c3d2db3480f31122db563f96"
        )
        private final String activityId;
        @ApiModelProperty(
                value = "활동명",
                notes = "관련된 활동의 활동명을 표시합니다.",
                example = "삼척에서 부유쓰레기 제거활동!"
        )
        private final String activityTitle;
        @ApiModelProperty(
                value = "메세지 내용",
                notes = "메세지 내용에 대해 표시합니다.",
                example = "안녕하세요. 개인메시지입니다."
        )
        private final String messageBody;
        @ApiModelProperty(
                value = "관련 활동 쓰레기 종류",
                notes = "관련된 활동의 쓰레기 종류를 표시합니다.",
                example = "FLOATING"
        )
        private final GarbageCategory garbageCategory;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "Asia/Seoul")
        @ApiModelProperty(
                value = "메세지 보낸 시각",
                notes = "메세지 보낸시각을 표시합니다",
                example = "2023-05-05T10:15:23"
        )
        private final LocalDateTime messageSentAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "Asia/Seoul")
        @ApiModelProperty(
                value = "활동 시작시각",
                notes = "활동 시작시각을 표시합니다.",
                example = "2023-05-10T10:00:00"
        )
        private final LocalDateTime activityStartAt;
        @ApiModelProperty(
                value = "메세지 읽음 여부",
                notes = "메세지를 받은 사람의 메세지 읽음 여부를 표시합니다.",
                example = "true"
        )
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
