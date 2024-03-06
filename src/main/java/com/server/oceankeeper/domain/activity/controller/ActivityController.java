package com.server.oceankeeper.domain.activity.controller;

import com.server.oceankeeper.domain.activity.dao.HostActivityDto;
import com.server.oceankeeper.domain.activity.dto.request.*;
import com.server.oceankeeper.domain.activity.dto.response.*;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.activity.service.ActivityMessageFacadeService;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {
    private final ActivityService activityService;
    private final ActivityMessageFacadeService activityMessageService;

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
            @ApiParam(name = "status", value = "open/close 중 하나. 활동 상태")
            @RequestParam(value = "status", required = false) String status,
            @ApiParam(name = "role", value = "crew/host 중 하나. 없으면 전체")
            @RequestParam(value = "role", required = false) String role,
            @ApiParam(name = "size", value = "한번에 조회할 갯수. 없으면 5개")
            @RequestParam(value = "size", required = false) Integer size) {
        if (status != null)
            status = status.toLowerCase();
        if (role != null)
            role = role.toLowerCase();
        MyActivityDto response = activityMessageService.getMyActivities(userId, activityId, status, role, size);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "활동 보기 [권한 필요]", notes = "활동을 간략하게 보여줍니다.",
            response = GetActivityResDto.class, responseContainer = "List")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<GetActivityResDto>> getActivity(
            @ApiParam(name = "status", value = "open/closed/recruitment-closed 중 하나로 확인 대상 활동 상태. 필터링 안하면 전체")
            @RequestParam(required = false) String status,
            @ApiParam(name = "location-tag", value = "WEST/EAST/SOUTH/JEJU/ETC 중 하나로 활동 지역태그. 필터링 안하면 전체")
            @RequestParam(value = "location-tag", required = false)
            LocationTag locationTag,
            @ApiParam(name = "garbage-category", value = "COASTAL/FLOATING/DEPOSITED/ETC 중 하나로 쓰레기 종류. 필터링 안하면 전체")
            @RequestParam(value = "garbage-category", required = false)
            GarbageCategory garbageCategory,
            @ApiParam(name = "size", value = "한번에 확인할 페이지 사이즈. 디폴트 1", example = "5")
            @RequestParam(value = "size", required = false) Integer pageSize,
            @ApiParam(name = "activity-id", value = "activity 아이디")
            @RequestParam(value = "activity-id", required = false) String activityId) {
        if (status != null)
            status = status.toLowerCase();
        GetActivityResDto response = activityService.getActivities(activityId, status, locationTag, garbageCategory, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "특정 활동 상세 보기 [권한 필요]", notes = "특정한 활동 하나를 상세하게 보여줍니다.",
            response = ActivityDetailResDto.class)
    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<ActivityDetailResDto>> getActivityDetail(
            @ApiParam(name = "activity-id", value = "activity 아이디", defaultValue = "11ee2964f8473afb9cf1650479121d20", required = true)
            @RequestParam("activity-id") String activityId) {
        ActivityDetailResDto response = activityService.getActivityDetail(activityId);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "특정 활동 지원서 보기 [권한 필요]", notes = "특정한 활동 지원서 하나를 상세하게 보여줍니다.",
            response = ApplicationDto.class)
    @GetMapping(value = "/detail/application", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<ApplicationDto>> getApplication(@RequestParam("application-id") String applicationId) {
        ApplicationDto response = activityService.getApplication(applicationId);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "지원자의 활동 신청서 및 활동 이력 보기 [권한 필요]", notes = "지원자의 활동 지원서 하나를 상세하게 보여줍니다.",
            response = FullApplicationResDto.class)
    @GetMapping(value = "/detail/application/full", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<FullApplicationResDto>> getFullApplication(@RequestParam("application-id") String applicationId,
                                                                                 HttpServletRequest servletRequest) {
        FullApplicationResDto response = activityService.getFullApplication(applicationId, servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "활동 등록 [권한 필요]", notes = "활동을 등록합니다.", response = RegisterActivityResDto.class)
    @PostMapping(value = "/recruitment",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<RegisterActivityResDto>> registerActivity(@RequestBody @Valid RegisterActivityReqDto activity,
                                                                                BindingResult bindingResult) {
        RegisterActivityResDto response = activityService.registerActivity(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse(response));
    }

    @ApiOperation(value = "활동 지원 [권한 필요]", notes = "활동에 지원합니다", response = ApplyActivityResDto.class)
    @PostMapping(value = "/recruitment/application",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<ApplyActivityResDto>> applyActivity(@RequestBody @Valid ApplyApplicationReqDto activity,
                                                                          BindingResult bindingResult) {
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
            HttpServletRequest request, BindingResult bindingResult) {
        activityService.modifyActivity(activityId, activity, request);
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
            HttpServletRequest request, BindingResult bindingResult) {
        activityService.modifyApplication(applicationId, activity, request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createPatchResponse("활동 지원서 수정 완료"));
    }

    @ApiOperation(value = "마지막 지원서 불러오기 [권한 필요]", notes = "마지막 지원서를 불러옵니다.",
            response = ApplicationDto.class)
    @GetMapping(value = "/recruitment/application/last",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<ApplicationDto>> getLastApplication(HttpServletRequest request) {
        ApplicationDto response = activityService.getLastApplication(request);
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
        activityService.cancelApplication(applicationId, request);
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
        activityService.cancelActivity(activityId, request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createDeleteResponse("활동 모집 삭제 완료"));
    }

    @ApiOperation(value = "요청자가 호스트인 활동의 활동명 불러오기 [권한 필요]", notes = "요청자가 호스트인 활동명을 불러옵니다",
            response = HostActivityDto.class)
    @GetMapping(value = "/recruitment/host/activity-name",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<HostActivityDto>> getHostActivity(HttpServletRequest request) {
        HostActivityDto response = activityService.getHostActivityName(request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "요청자가 호스트인 활동의 크루원 닉네임 불러오기 [권한 필요]", notes = "요청자가 호스트인 활동의 크루원 닉네임을 불러옵니다",
            response = CrewActivityDto.class)
    @GetMapping(value = "/recruitment/host/crew-nickname",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<CrewActivityDto>> getCrewNickNames(
            @ApiParam(value = "특정 활동 id", required = true, defaultValue = "11ee2962ed293b2a869b0f30e7d4f7c1")
            @RequestParam(value = "activity-id") String activityId, HttpServletRequest request) {
        CrewActivityDto response = activityService.getCrewInfo(activityId, request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "크루원 정보 불러오기 [권한 필요]", notes = "요청자가 특정 활동의 크루원 정보를 불러옵니다",
            response = CrewInfoDetailDto.class)
    @GetMapping(value = "/recruitment/host/crew-info",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<CrewInfoDetailDto>> getCrewInfoDetail(
            @ApiParam(value = "특정 활동 id", required = true, defaultValue = "11ee2962ed293b2a869b0f30e7d4f7c1")
            @RequestParam(value = "activity-id") String activityId, HttpServletRequest request) {
        CrewInfoDetailDto response = activityService.getCrewInfoDetail(activityId, request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "크루원 정보 엑셀 저장하기 [권한 필요]", notes = "요청자가 특정 활동의 크루원 정보가 저장된 파일을 다운받습니다.",
            response = ByteArrayResource.class)
    @GetMapping(value = "/recruitment/host/crew-info-file"
            //        produces = "application/vnd.ms-excel"
    )
    //public ByteArrayResource getCrewInfoFile(
    public void getCrewInfoFile(
            @ApiParam(value = "특정 활동 id", required = true, defaultValue = "11ee2962ed293b2a869b0f30e7d4f7c1")
            @RequestParam(value = "activity-id") String activityId, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        CrewInfoFileDto response = activityService.getCrewInfoFile(activityId, request);
//        return response.getCrewInfo();
        response.setContentType("application/vnd.ms-excel");
        activityService.getCrewInfoFile(activityId, request, response);

    }

    @ApiOperation(value = "크루원 승인 설정[권한 필요]", notes = "요청자가 특정 활동의 크루원을 변경합니다.",
            response = ApplicationSettingResDto.class)
    @PostMapping(value = "/recruitment/host/crew-status",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<ApplicationSettingResDto>> setCrewAcceptance(
            @RequestBody ApplicationSettingReqDto request, HttpServletRequest servletRequest) {
        ApplicationSettingResDto response = activityMessageService.setApplicationStatus(request, servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createPostResponse(response));
    }

    @PostMapping(value = "/get")
    public void test(
            @RequestParam(value = "activity-id") String activityId, HttpServletRequest servletRequest) {
        activityService.startActivitySoon(activityId);
    }

    @PostMapping(value = "/cal")
    public void test2() {
        activityService.reCalculate();
    }

    @PostMapping(value = "/del")
    public void test3(){activityService.handleActivityInfoDeleteEvent();}

    @PostMapping(value = "/close")
    public void test4(){activityService.testCloseActivity();}
}