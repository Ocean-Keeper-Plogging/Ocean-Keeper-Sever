package com.server.oceankeeper.domain.message.controller;

import com.server.oceankeeper.domain.message.dto.request.MessageReadReqDto;
import com.server.oceankeeper.domain.message.dto.request.MessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.response.MessageSendResDto;
import com.server.oceankeeper.domain.message.dto.response.PostResDto;
import com.server.oceankeeper.domain.message.dto.response.PrivateMessageSendResDto;
import com.server.oceankeeper.domain.message.service.MessageService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @ApiOperation(value = "유저의 쪽지함 확인 [권한 필요]",
            notes = "특정 유저의 쪽지함 내역을 보여줍니다", response = PostResDto.class)
    @GetMapping(value = "/inbox", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<PostResDto> getAllMessage(
            @ApiParam(name = "user", value = "메세지 보내는 유저 아이디")
            @RequestParam("user") String userId,
            @ApiParam(name = "size", value = "메세지 개수. 기본 5")
            @RequestParam(required = false) Integer size,
            @ApiParam(name = "message id", value = "메세지 아이디. 기본 1")
            @RequestParam(required = false) Long id,
            @ApiParam(name = "type", value = "메세지 타입(REJECT,NOTICE,PRIVATE,SENT 중 하나), 지정안하면 전체")
            @RequestParam(value = "type", required = false) String type, HttpServletRequest request) throws Exception {
        PostResDto response = messageService.getInbox(userId, id, type, size, request);
        return APIResponse.createGetResponse(response);
    }

    @ApiOperation(value = "쪽지 보내기 [권한 필요]",
            notes = "쪽지를 보냅니다", response = MessageSendResDto.class)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<MessageSendResDto> sendMessage(@Valid @RequestBody MessageSendReqDto message, BindingResult result, HttpServletRequest request) {
        MessageSendResDto response = messageService.sendMessage(message, request);
        return APIResponse.createPostResponse(response);
    }

    @ApiOperation(value = "상세 쪽지의 읽음 상태를 변경합니다 [권한 필요]",
            notes = "상세 쪽지의 읽음 상태를 변경합니다", response = PrivateMessageSendResDto.class)
    @PostMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<Boolean> getMessage(@Valid @RequestBody MessageReadReqDto data, BindingResult result, HttpServletRequest request) {
        boolean response = messageService.changeReadFlag(data, request);
        return APIResponse.createPostResponse(response);
    }

    @ApiOperation(value = "쪽지 삭제 [권한 필요]",
            notes = "쪽지를 삭제합니다", response = Boolean.class)
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<Boolean> deleteMessage(@RequestParam(value = "message-id") Long messageId, HttpServletRequest request) {
        boolean response = messageService.delete(messageId, request);
        return APIResponse.createDeleteResponse(response);
    }
}