package com.server.oceankeeper.domain.message.controller;

import com.server.oceankeeper.domain.notification.dto.FCMRequestDto;
import com.server.oceankeeper.domain.notification.dto.MessagePreFormat;
import com.server.oceankeeper.domain.notification.service.FCMService;
import com.server.oceankeeper.domain.user.service.UserService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test/message")
@RequiredArgsConstructor
public class TestMessageController {
    private final FCMService service;
    private final UserService userService;

    //TODO: delete it
    @ApiOperation(value = "유저에게 알림보내기 [테스트용도]",
            notes = "특정 유저(닉네임)에게 fcm 알림을 날립니다", response = String.class)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<String>> sendTestMessage(
            @ApiParam(name = "nickname", value = "메세지 보내는 유저 닉네임")
            @RequestParam String nickname,
            @ApiParam(name = "contents", value = "메세지 내용. 없으면 맘대로 보냄.")
            @RequestParam(required = false) String contents) {
        String deviceToken = userService.findNickname(nickname);
        String con =Arrays.stream(MessagePreFormat.class.getEnumConstants()).collect(Collectors.toList()).get((int)(Math.random()*100)%5).getValue();
        String response = service.sendFCMMessage(new FCMRequestDto(deviceToken,contents!=null ? contents: con));
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createPostResponse(response));
    }
}