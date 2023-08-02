package com.server.oceankeeper.domain.statistics.controller;

import com.server.oceankeeper.domain.statistics.dto.ActivityInfoResDto;
import com.server.oceankeeper.domain.statistics.service.ActivityInfoService;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.util.TokenUtil;
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
    private final TokenUtil tokenUtil;

    @ApiOperation(value = "활동 정보 표시 [권한 필요]", notes = "현재까지 활동한 활동 정보를 표시합니다.", response = ActivityInfoResDto.class)
    @GetMapping("/activity-info/user/{userId}")
    public ResponseEntity<ActivityInfoResDto> getActivityInfo(@PathVariable String userId, HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        ActivityInfoResDto response = activityInfoService.getActivityInfo(userId, user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
