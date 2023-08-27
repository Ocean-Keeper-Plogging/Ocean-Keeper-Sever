package com.server.oceankeeper.domain.message.messageController;

import com.server.oceankeeper.domain.message.dto.request.MessageDetailReqDto;
import com.server.oceankeeper.domain.message.dto.request.MessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.request.PrivateMessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.response.MessageDetailResDto;
import com.server.oceankeeper.domain.message.dto.response.MessageSendResDto;
import com.server.oceankeeper.domain.message.dto.response.PostResDto;
import com.server.oceankeeper.domain.message.dto.response.PrivateMessageSendResDto;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.service.MessageService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @ApiOperation(value = "유저의 전체 쪽지 확인 [권한 필요]",
            notes = "특정 유저의 전체 쪽지(개인쪽지+활동공지+거절쪽지) 내역을 보여줍니다", response = PostResDto.class)
    @GetMapping(value = "/inbox", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<PostResDto> getAllMessage(
            @RequestParam("user") String userId,
            @RequestParam Integer size,
            @RequestParam Long id,
            @RequestParam("type") MessageType type, HttpServletRequest request) throws Exception {
        PostResDto response = messageService.getMailing(userId, id, type, size, request);
        return APIResponse.createGetResponse(response);
    }

    @ApiOperation(value = "쪽지 보내기 [권한 필요]",
            notes = "쪽지를 보냅니다", response = MessageSendResDto.class)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<MessageSendResDto> sendMessage(@RequestBody MessageSendReqDto message, HttpServletRequest request) {
        MessageSendResDto response = messageService.sendMessage(message, request);
        return APIResponse.createPostResponse(response);
    }

    @ApiOperation(value = "개인 쪽지 보내기 [권한 필요]",
            notes = "개인쪽지를 보냅니다", response = PrivateMessageSendResDto.class)
    @PostMapping(value = "/message/private", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<PrivateMessageSendResDto> sendPrivateMessage(@RequestBody PrivateMessageSendReqDto message) {
        PrivateMessageSendResDto response = messageService.sendPrivateMessage(message);
        return APIResponse.createPostResponse(response);
    }

    @ApiOperation(value = "상세 쪽지 확인 [권한 필요]",
            notes = "특정 쪽지의 정보를 확인합니다", response = PrivateMessageSendResDto.class)
    @GetMapping(value = "/message", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<MessageDetailResDto> getMessage(@RequestParam(value = "message-id") Long messageId) {
        MessageDetailResDto response = messageService.getMessage(messageId);
        return APIResponse.createPostResponse(response);
    }
}
