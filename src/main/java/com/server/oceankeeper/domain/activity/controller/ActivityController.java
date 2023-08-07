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
import com.server.oceankeeper.global.response.APIResponse;
import com.server.oceankeeper.util.TokenUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    public ResponseEntity<APIResponse<MyScheduledActivitiesDto>> getMyScheduleActivity(
            @ApiParam(value = "특정 활동 id", required = true, defaultValue = "11ee2962ed293b2a869b0f30e7d4f7c1")
            @PathVariable String userId) {
        List<MyScheduledActivityDto> list = activityService.getMyScheduleActivity(userId);
        MyScheduledActivitiesDto response = new MyScheduledActivitiesDto(list);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "내 활동 보기 [권한 필요]", notes = "내가 참여하거나 생성한 활동을 정렬하여 보여줍니다", response = MyActivityDto.class)
    @GetMapping(value = "/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<MyActivityDto>> getMyActivity(
            @ApiParam(value = "특정 활동 id", required = true, defaultValue = "11ee2962ed293b2a869b0f30e7d4f7c1")
            @PathVariable String userId,
            @ApiParam(name = "activity-id", value = "특정 활동 id",
                    defaultValue = "11ee2964f8473afb9cf1650479121d20")
            @RequestParam(value = "activity-id", required = false) String activityId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer size) {
        MyActivityDto response = activityService.getMyActivities(userId, activityId, status, size);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "활동 보기 [권한 필요]", notes = "활동을 간략하게 보여줍니다.", response = GetActivityResDto.class, responseContainer = "List")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<GetActivityResDto>> getActivity(
            @ApiParam(name = "status", value = "open/closed/all 중 하나로 확인 대상 활동 상태")
            @RequestParam(required = false) String status,
            @ApiParam(name = "location-tag", value = "WEST/EAST/SOUTH/JEJU/ETC 중 하나로 활동 지역태그")
            @RequestParam(value = "location-tag", required = false)
            LocationTag locationTag,
            @ApiParam(name = "garbage-category", value = "COASTAL/FLOATING/DEPOSITED/ETC 중 하나로 쓰레기 종류")
            @RequestParam(value = "garbage-category", required = false)
            GarbageCategory garbageCategory,
            @ApiParam(name = "size", value = "한번에 확인할 페이지 사이즈.", defaultValue = "1", example = "5")
            @RequestParam(value = "size", required = false) Integer pageSize,
            @ApiParam(name = "activity-id", value = "activity 아이디")
            @RequestParam(value = "activity-id", required = false) String activityId) {
        if (status != null)
            status = status.toLowerCase();
        GetActivityResDto response = activityService.getActivities(activityId, status, locationTag, garbageCategory, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "특정 활동 상세 보기 [권한 필요]", notes = "특정한 활동 하나를 상세하게 보여줍니다.", response = ActivityDetailResDto.class)
    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<ActivityDetailResDto>> getActivityDetail(
            @ApiParam(name = "activity-id", value = "activity 아이디", defaultValue = "11ee2964f8473afb9cf1650479121d20", required = true)
            @RequestParam("activity-id") String activityId) {
        ActivityDetailResDto response = activityService.getActivityDetail(activityId);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "특정 활동 지원서 보기 [권한 필요]", notes = "특정한 활동 지원서 하나를 상세하게 보여줍니다.", response = ApplicationReqDto.class)
    @GetMapping(value = "/detail/application", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<ApplicationReqDto>> getApplication(@RequestParam("application-id") String applicationId) {
        ApplicationReqDto response = activityService.getApplication(applicationId);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "활동 등록 [권한 필요]", notes = "활동을 등록합니다.", response = RegisterActivityResDto.class)
    @PostMapping(value = "/recruitment",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<RegisterActivityResDto>> registerActivity(@RequestBody @Valid RegisterActivityReqDto activity) {
        RegisterActivityResDto response = activityService.registerActivity(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse(response));
    }

    @ApiOperation(value = "활동 지원 [권한 필요]", notes = "활동에 지원합니다", response = ApplyActivityResDto.class)
    @PostMapping(value = "/recruitment/application",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<ApplyActivityResDto>> applyActivity(@RequestBody @Valid ApplyApplicationReqDto activity) {
        ApplyActivityResDto response = activityService.applyActivity(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse(response));
    }

    @ApiOperation(value = "활동 수정 [권한 필요]", notes = "작성한 활동을 수정합니다", response = String.class)
    @PatchMapping(value = "/recruitment/{activityId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<String>> modifyActivity(
            @ApiParam(name = "activityId", value = "지원서 id", defaultValue = "11ee2964f8473afb9cf1650479121d20", required = true)
            @PathVariable String activityId,
            @RequestBody @Valid ModifyActivityReqDto activity,
            HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        activityService.modifyActivity(activityId, activity, user);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createPatchResponse("활동 수정 완료"));
    }

    @ApiOperation(value = "활동 지원서 수정 [권한 필요]", notes = "작성한 활동 지원서를 수정합니다", response = String.class)
    @PatchMapping(value = "/recruitment/application/{applicationId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<String>> modifyApplication(
            @ApiParam(name = "applicationId", value = "지원서 id", defaultValue = "11ee2968b912f9be9cf11179b5b80610", required = true)
            @PathVariable String applicationId,
            @RequestBody @Valid ModifyApplicationReqDto activity,
            HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        activityService.modifyApplication(applicationId, activity, user);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createPatchResponse("활동 지원서 수정 완료"));
    }

    @ApiOperation(value = "마지막 지원서 불러오기 [권한 필요]", notes = "마지막 지원서를 불러옵니다.",
            response = ApplicationReqDto.class)
    @GetMapping(value = "/recruitment/application/last",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<ApplicationReqDto>> getLastApplication(HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        ApplicationReqDto response = activityService.getLastApplication(user);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "활동 지원 취소 [권한 필요]", notes = "해당 활동 지원을 취소합니다.",
            response = String.class)
    @DeleteMapping(value = "/recruitment/application/cancel",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<String>> cancelApplication(
            @ApiParam(name = "application-id", value = "지원 취소할 지원서 id", defaultValue = "11ee2968b912f9be9cf11179b5b80610", required = true)
            @RequestParam("application-id") String applicationId,
            HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        activityService.cancelApplication(applicationId, user);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createDeleteResponse("활동 지원서 삭제 완료"));
    }

    @ApiOperation(value = "활동 모집 취소 [권한 필요]", notes = "해당 활동 모집을 취소합니다.",
            response = String.class)
    @DeleteMapping(value = "/recruitment",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<String>> cancelActivity(
            @ApiParam(name = "activity-id", value = "취소할 활동 id", defaultValue = "11ee2964f8473afb9cf1650479121d20", required = true)
            @RequestParam("activity-id") String activityId,
            HttpServletRequest request) {
        OUser user = tokenUtil.getProviderInfoFromHeader(request);
        activityService.cancelActivity(activityId, user);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createDeleteResponse("활동 모집 삭제 완료"));
    }
}