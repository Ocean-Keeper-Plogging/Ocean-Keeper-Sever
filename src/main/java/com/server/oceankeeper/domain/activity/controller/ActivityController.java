package com.server.oceankeeper.domain.activity.controller;

import com.server.oceankeeper.domain.activity.dto.request.ApplyActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.request.RegisterActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.response.*;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {
    private final ActivityService activityService;

    @ApiOperation(value = "내 활동 보기 [권한 필요]", notes = "내가 참여하거나 생성한 활동을 정렬하여 보여줍니다", response = MyActivitiesDto.class)
    @GetMapping(value = "/user/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyActivitiesDto> getMyActivity(@PathVariable String id) {
        List<MyActivityDto> list = activityService.getMyActivity(id);
        return new ResponseEntity<MyActivitiesDto>(new MyActivitiesDto(list), HttpStatus.OK);
    }

    @ApiOperation(value = "활동 보기 [권한 필요]", notes = "활동을 간략하게 보여줍니다.", response = ActivityResDto.class)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ActivityResDto>> getActivity(@RequestParam String status,
                                                            @RequestParam(value = "location-tag", required = false)
                                                            LocationTag locationTag,
                                                            @RequestParam(value = "garbage-category", required = false)
                                                            GarbageCategory garbageCategory,
                                                            Pageable pageable) {
        List<ActivityResDto> response = activityService.getAllActivities(status, locationTag, garbageCategory, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/recruitment",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterActivityResDto> registerActivity(@RequestBody @Valid RegisterActivityReqDto activity) {
        RegisterActivityResDto response = activityService.registerActivity(activity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/recruitment/application",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApplyActivityResDto> applyActivity(@RequestBody @Valid ApplyActivityReqDto activity) {
        ApplyActivityResDto response = activityService.applyActivity(activity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}