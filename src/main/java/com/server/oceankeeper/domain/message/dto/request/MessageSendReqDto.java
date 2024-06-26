package com.server.oceankeeper.domain.message.dto.request;

import com.server.oceankeeper.domain.message.entity.MessageSentType;
import com.server.oceankeeper.domain.message.entity.MessageType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MessageSendReqDto {
    @ApiModelProperty(
            value = "대상 닉네임",
            notes = "리스트로 전달해야함",
            example = "[\"user1\",\"user2\"]"
    )
    @NotEmpty
    private final List<String> targetNicknames;

    @ApiModelProperty(
            value = "메세지 타입",
            notes = "REJECT(거절),NOTICE(공지),PRIVATE(개인) 중 하나",
            example = "NOTICE"
    )
    @NotNull
    private final MessageSentType type;

    @ApiModelProperty(
            value = "관련 활동 아이디",
            example = "11ee4241f92c46fdb46d2591c8088427"
    )
    @NotEmpty
    private final String activityId;

    @ApiModelProperty(
            value = "내용",
            notes = "쪽지 내용을 입력합니다.",
            example = "안녕하세요. 어서오세요."
    )
    @NotEmpty
    private final String contents;
}
