package com.server.oceankeeper.domain.activity.controller;

import com.server.oceankeeper.domain.activity.dto.request.ApplyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.request.ModifyActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.request.ModifyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.request.RegisterActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.response.*;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.response.ApiResponse;
import com.server.oceankeeper.util.TokenUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {
    private final ActivityService activityService;
    private final TokenUtil tokenUtil;

    @ApiOperation(value = "다가오는 일정 조회 [권한 필요]",
            notes = "내가 참여하거나 생성한 활동을 정렬하여 보여줍니다", response = MyScheduledActivitiesDto.class)
    @GetMapping(value = "/schedule/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyScheduledActivitiesDto> getMyScheduleActivity(@PathVariable String userId) {
        List<MyScheduledActivityDto> list = activityService.getMyScheduleActivity(userId);
        return new ResponseEntity<>(new MyScheduledActivitiesDto(list), HttpStatus.OK);
    }

    @ApiOperation(value = "내 활동 보기 [권한 필요]", notes = "내가 참여하거나 생성한 활동을 정렬하여 보여줍니다", response = MyScheduledActivitiesDto.class)
    @GetMapping(value = "/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyActivityDto> getMyActivity(@PathVariable String userId,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) Integer size,
                                                       @RequestParam(value = "activity-id", required = false) String activityId) {
        MyActivityDto result = activityService.getMyActivities(userId, activityId, status, size);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "활동 보기 [권한 필요]", notes = "활동을 간략하게 보여줍니다.", response = ScheduledActivityResDto.class)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScheduledActivityResDto>> getActivity(@RequestParam String status,
                                                                     @RequestParam(value = "location-tag", required = false)
                                                                     LocationTag locationTag,
                                                                     @RequestParam(value = "garbage-category", required = false)
                                                                     GarbageCategory garbageCategory,
                                                                     @RequestParam(value = "size", required = false) Integer pageSize,
                                                                     @RequestParam(value = "activity-id", required = false) String activityId) {
        List<ScheduledActivityResDto> response = activityService.getActivities(activityId, status, locationTag, garbageCategory, pageSize);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 활동 지원서 보기 [권한 필요]", notes = "특정한 활동 지원서 하나를 상세하게 보여줍니다.",
            response = ActivityDetailResDto.class)
    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ActivityDetailResDto> getActivityDetail(@RequestParam("activity-id") String activityId) {
        ActivityDetailResDto response = activityService.getActivityDetail(activityId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 활동 상세 보기 [권한 필요]", notes = "특정한 활동 하나를 상세하게 보여줍니다.", response = ApplicationReqDto.class)
    @GetMapping(value = "/detail/application", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApplicationReqDto> getApplication(@RequestParam("application-id") String applicationId) {
        ApplicationReqDto response = activityService.getApplication(applicationId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "활동 등록 [권한 필요]", notes = "활동을 등록합니다.", response = RegisterActivityResDto.class)
    @PostMapping(value = "/recruitment",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterActivityResDto> registerActivity(@RequestBody @Valid RegisterActivityReqDto activity) {
        RegisterActivityResDto response = activityService.registerActivity(activity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "활동 지원 [권한 필요]", notes = "활동에 지원합니다", response = ApplyActivityResDto.class)
    @PostMapping(value = "/recruitment/application",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApplyActivityResDto> applyActivity(@RequestBody @Valid ApplyApplicationReqDto activity) {
        ApplyActivityResDto response = activityService.applyActivity(activity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "활동 수정 [권한 필요]", notes = "작성한 활동을 수정합니다", response = ApiResponse.class)
    @PatchMapping(value = "/recruitment/{activityId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> modifyActivity(@PathVariable String activityId,
                                                      @RequestBody @Valid ModifyActivityReqDto activity,
                                                      HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        activityService.modifyActivity(activityId, activity, user);
        return new ResponseEntity<>(ApiResponse.createSuccess("활동 수정 완료"), HttpStatus.OK);
    }

    @ApiOperation(value = "활동 지원서 수정 [권한 필요]", notes = "작성한 활동 지원서를 수정합니다", response = ApiResponse.class)
    @PatchMapping(value = "/recruitment/application/{applicationId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> modifyApplication(@PathVariable String applicationId,
                                                         @RequestBody @Valid ModifyApplicationReqDto activity,
                                                         HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        activityService.modifyApplication(applicationId, activity, user);
        return new ResponseEntity<>(ApiResponse.createSuccess("활동 지원서 수정 완료"), HttpStatus.OK);
    }

    @ApiOperation(value = "마지막 지원서 불러오기 [권한 필요]", notes = "마지막 지원서를 불러옵니다.",
            response = ApplicationReqDto.class)
    @GetMapping(value = "/recruitment/application/last",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApplicationReqDto> getLastApplication(HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        ApplicationReqDto response = activityService.getLastApplication(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "활동 지원 취소 [권한 필요]", notes = "해당 활동 지원을 취소합니다.",
            response = ApiResponse.class)
    @DeleteMapping(value = "/recruitment/application/cancel",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> cancelApplication(@RequestParam("application-id") String applicationId,
                                                         HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        activityService.cancelApplication(applicationId, user);
        return new ResponseEntity<>(ApiResponse.createSuccess("활동 지원서 삭제 완료"), HttpStatus.OK);
    }

    @ApiOperation(value = "활동 모집 취소 [권한 필요]", notes = "해당 활동 모집을 취소합니다.",
            response = ApiResponse.class)
    @DeleteMapping(value = "/recruitment",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> cancelActivity(@RequestParam("activity-id") String activityId,
                                                         HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        activityService.cancelActivity(activityId, user);
        return new ResponseEntity<>(ApiResponse.createSuccess("활동 모집 삭제 완료"), HttpStatus.OK);
    }
}