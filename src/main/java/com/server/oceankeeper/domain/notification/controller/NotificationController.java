package com.server.oceankeeper.domain.notification.controller;

import com.server.oceankeeper.domain.notification.dto.NotificationAlarmDto;
import com.server.oceankeeper.domain.notification.dto.NotificationResDto;
import com.server.oceankeeper.domain.notification.service.NotificationService;
import com.server.oceankeeper.domain.user.dto.LoginResDto;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @ApiOperation(value = "알림 리스트 확인[권한 필요]", notes = "알림 리스트를 확인합니다.", response = NotificationResDto.class)
    @GetMapping(value = "/notification/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<NotificationResDto>> getNotification(
            @ApiParam(value = "유저 id", required = true, defaultValue = "11ee2962ed293b2a869b0f30e7d4f7c1")
            @PathVariable String userId,
            @ApiParam(name = "id", value = "마지막 조회된 id. 없으면 null")
            @RequestParam(value = "id", required = false) Long id,
            @ApiParam(name = "size", value = "한번에 조회할 갯수. 없으면 20개")
            @RequestParam(value = "size", required = false) Integer size,
            HttpServletRequest request) {
        NotificationResDto response = notificationService.getNotificationList(userId, id, size, request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "firebase 알람 수신 여부 설정[권한 필요]", notes = "firebase 알람 수신 여부를 설정합니다.", response = NotificationAlarmDto.class)
    @PostMapping(value = "/notification/user/{userId}/alarm",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<NotificationAlarmDto>> setNotification(
            @ApiParam(value = "유저 id", required = true, defaultValue = "11ee2962ed293b2a869b0f30e7d4f7c1")
            @PathVariable String userId,
            @ApiParam(name = "alarm", value = "notification 알람 설정 여부")
            @RequestParam(value = "alarm") Boolean alarm,
            HttpServletRequest request) {
        NotificationAlarmDto response = notificationService.setNotification(userId, alarm, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse(response));
    }

    @ApiOperation(value = "firebase 알람 수신 여부 확인[권한 필요]", notes = "firebase 알람 수신 여부를 확인합니다.", response = NotificationAlarmDto.class)
    @GetMapping(value = "/notification/user/{userId}/alarm",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<NotificationAlarmDto>> getNotification(
            @ApiParam(value = "유저 id", required = true, defaultValue = "11ee2962ed293b2a869b0f30e7d4f7c1")
            @PathVariable String userId, HttpServletRequest request) {
        NotificationAlarmDto response = notificationService.getNotification(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }
}
