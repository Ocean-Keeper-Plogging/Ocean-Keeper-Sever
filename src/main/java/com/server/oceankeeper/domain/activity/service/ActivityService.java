package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityDetail;
import com.server.oceankeeper.domain.activity.dto.request.ApplyActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.request.MyActivityDto;
import com.server.oceankeeper.domain.activity.dto.request.RegisterActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.response.ActivityResDto;
import com.server.oceankeeper.domain.activity.dto.response.ApplyActivityResDto;
import com.server.oceankeeper.domain.activity.dto.response.RegisterActivityResDto;
import com.server.oceankeeper.domain.activity.repository.ActivityDetailRepository;
import com.server.oceankeeper.domain.activity.repository.ActivityRepository;
import com.server.oceankeeper.domain.crew.CrewRole;
import com.server.oceankeeper.domain.crew.CrewService;
import com.server.oceankeeper.domain.crew.Crews;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityDetailRepository activityDetailRepository;
    private final UserRepository userRepository;

    private final CrewService crewService;

    @Transactional
    public List<MyActivityDto> getMyActivity(String id) {
        OUser user = userRepository.findByUuid(UUID.fromString(id)).orElseThrow(RuntimeException::new);
        List<Crews> crews = crewService.findByUser(user);
        List<MyActivityDto> response = new ArrayList<>();
        for (Crews crew : crews) {
            Activity a = crew.getActivity();
            ActivityDetail ad = activityDetailRepository.findByActivity(a)
                    .orElseThrow(() -> new IdNotFoundException("해당 아이디의 활동이 없습니다."));
        }

        return response;
    }

    private Integer caculateDday(LocalDateTime startAt) {
        //TODO : implementation
        return 1;
    }

    public ActivityResDto getOpenActivityByLocationByGarbageCategory(Object o, Pageable pageable) {
        return null;
    }

    public ActivityResDto getOpenActivityByLocation(Object o, Pageable pageable) {
        return null;
    }

    public ActivityResDto getOpenActivityByGarbageCategory(Object o, Pageable pageable) {
        return null;
    }

    public ActivityResDto getClosedActivityByLocation(Object o, Pageable pageable) {
        return null;
    }

    public ActivityResDto getClosedActivityByGarbageCategory(Object o, Pageable pageable) {
        return null;
    }

    @Transactional
    public RegisterActivityResDto registerActivity(RegisterActivityReqDto request) {
        if(true){
            return null;
        }
        OUser user = userRepository.findByUuid(UUIDGenerator.changeUuidFromString(request.getUserId()))
                .orElseThrow(() -> new IdNotFoundException("해당 아이디가 존재하지 않습니다."));

        Activity activity = request.toActivityEntity();
        activityRepository.save(activity);

        ActivityDetail activityDetail = request.toActivityDetailEntity();
        activityDetail.setActivity(activity);
        activityDetailRepository.save(activityDetail);

        crewService.addCrew(activity, user, CrewRole.HOST);
        return new RegisterActivityResDto("3L");
    }

    @Transactional
    public ApplyActivityResDto applyActivity(ApplyActivityReqDto activity) {
        return null;
    }
}
