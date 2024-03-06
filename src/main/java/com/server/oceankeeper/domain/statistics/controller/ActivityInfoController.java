package com.server.oceankeeper.domain.statistics.controller;

import com.server.oceankeeper.domain.statistics.dto.ActivityInfoResDto;
import com.server.oceankeeper.domain.statistics.service.ActivityInfoService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/activity")
public class ActivityInfoController {
    private final ActivityInfoService activityInfoService;

    @ApiOperation(value = "활동 정보 표시 [권한 필요]", notes = "현재까지 활동한 활동 정보를 표시합니다.", response = ActivityInfoResDto.class)
    @GetMapping("/activity-info/user/{userId}")
    public ResponseEntity<APIResponse<ActivityInfoResDto>> getActivityInfo(@PathVariable String userId, HttpServletRequest request) {
        ActivityInfoResDto response = activityInfoService.getUserActivityInfo(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }
}
