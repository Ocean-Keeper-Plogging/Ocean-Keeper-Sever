package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dto.request.ApplyActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.request.RegisterActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.response.*;
import com.server.oceankeeper.domain.activity.entity.*;
import com.server.oceankeeper.domain.activity.repository.ActivityDetailRepository;
import com.server.oceankeeper.domain.activity.repository.ActivityRepository;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.crew.service.CrewService;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityDetailRepository activityDetailRepository;
    private final UserRepository userRepository;
    private final CrewService crewService;

    @Transactional
    public List<MyActivityDto> getMyActivity(String id) {
        return crewService.findCrews(new MyActivityParam(LocalDateTime.now(), UUIDGenerator.changeUuidFromString(id), CrewStatus.IN_PROGRESS));
    }

    public List<ActivityResDto> getAllActivities(String status, LocationTag locationTag, GarbageCategory garbageCategory, Pageable pageable) {
        ActivityStatus activityStatus = ActivityStatus.getStatus(status);
        List<ActivityDao> response = activityRepository.findActivities(activityStatus, locationTag, garbageCategory, pageable);
        log.info("getAllActivities response :{}", response);
        if (response == null) {
            throw new ResourceNotFoundException("조회된 활동이 없습니다.");
        }
        return response.stream().map(r -> new ActivityResDto(
                UUIDGenerator.changeUuidToString(r.getActivityId()),
                r.getTitle(),
                r.getLocationTag(),
                r.getGarbageCategory(),
                r.getHostNickname(),
                r.getQuota(),
                r.getParticipants(),
                r.getActivityImageUrl())).collect(Collectors.toList());
    }

    @Transactional
    public RegisterActivityResDto registerActivity(RegisterActivityReqDto request) {
        log.info("registerActivity request :{}", request);

        OUser user = userRepository.findByUuid(UUIDGenerator.changeUuidFromString(request.getUserId()))
                .orElseThrow(() -> new IdNotFoundException("해당 아이디가 존재하지 않습니다."));

        Activity activity = request.toActivityEntity();
        activityRepository.save(activity);

        ActivityDetail activityDetail = request.toActivityDetailEntity();
        activityDetail.setActivity(activity);
        activityDetailRepository.save(activityDetail);

        crewService.addHost(activity, user);

        return new RegisterActivityResDto(UUIDGenerator.changeUuidToString(activity.getUuid()));
    }

    @Transactional
    public ApplyActivityResDto applyActivity(ApplyActivityReqDto request) {
        Activity activity = activityRepository.findByUuid(UUIDGenerator.changeUuidFromString(request.getActivityId()))
                .orElseThrow(() -> new IdNotFoundException("해당 활동이 존재하지 않습니다."));
        OUser applyUser = userRepository.findByUuid(UUIDGenerator.changeUuidFromString(request.getUserId()))
                .orElseThrow(() -> new IdNotFoundException("해당 유저가 존재하지 않습니다."));

        activity.addParticipant();
        Crews crew = crewService.addCrew(request, activity, applyUser);

        return new ApplyActivityResDto(
                UUIDGenerator.changeUuidToString(activity.getUuid())
                , UUIDGenerator.changeUuidToString(crew.getUuid()));
    }
}
