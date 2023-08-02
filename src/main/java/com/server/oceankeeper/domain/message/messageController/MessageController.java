package com.server.oceankeeper.domain.message.messageController;

import com.server.oceankeeper.domain.message.dto.request.MessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.request.PrivateMessageSendReqDto;
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

    @ApiOperation(value = "유저의 보낸 쪽지 확인 [권한 필요]",
            notes = "특정 유저의 보낸 쪽지 내역을 보여줍니다", response = PostResDto.class)
    @GetMapping(value = "/post/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<PostResDto> getMailing(@PathVariable String userId, HttpServletRequest request) throws Exception {
        PostResDto response = messageService.getMailing(userId, MessageType.POST, request);
        return APIResponse.createGetResponse(response);
    }

    @ApiOperation(value = "유저의 전체 쪽지 확인 [권한 필요]",
            notes = "특정 유저의 전체 쪽지(개인쪽지+활동공지쪽지) 내역을 보여줍니다", response = PostResDto.class)
    @GetMapping(value = "/inbox/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<PostResDto> getAllMessage(@PathVariable String userId, @RequestParam("type") MessageType type, HttpServletRequest request) throws Exception {
        PostResDto response = messageService.getMailing(userId, type, request);
        return APIResponse.createGetResponse(response);
    }

    @ApiOperation(value = "쪽지 보내기 [권한 필요]",
            notes = "쪽지를 보냅니다", response = MessageSendResDto.class)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<MessageSendResDto> sendMessage(@RequestBody MessageSendReqDto message) {
        MessageSendResDto response = messageService.sendMessage(message);
        return APIResponse.createPostResponse(response);
    }

    @ApiOperation(value = "개인 쪽지 보내기 [권한 필요]",
            notes = "개인쪽지를 보냅니다", response = PrivateMessageSendResDto.class)
    @PostMapping(value = "/message/private", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<PrivateMessageSendResDto> sendPrivateMessage(@RequestBody PrivateMessageSendReqDto message) {
        PrivateMessageSendResDto response = messageService.sendPrivateMessage(message);
        return APIResponse.createPostResponse(response);
    }
}
