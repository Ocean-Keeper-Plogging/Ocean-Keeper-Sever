package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dto.request.ApplyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.request.ModifyActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.request.ModifyApplicationReqDto;
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
import com.server.oceankeeper.global.exception.DuplicatedResourceException;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.IllegalRequestException;
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

    @Transactional
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

        OUser user = getUser(request.getUserId());

        Activity activity = request.toActivityEntity();
        activityRepository.save(activity);

        ActivityDetail activityDetail = request.toActivityDetailEntity();
        activityDetail.setActivity(activity);
        activityDetailRepository.save(activityDetail);

        crewService.addHost(activity, user);

        //TODO:Publish event to apply activity

        return new RegisterActivityResDto(UUIDGenerator.changeUuidToString(activity.getUuid()));
    }

    private OUser getUser(String userId) {
        return userRepository.findByUuid(UUIDGenerator.changeUuidFromString(userId))
                .orElseThrow(() -> new IdNotFoundException("해당 아이디가 존재하지 않습니다."));
    }

    @Transactional
    public ApplyActivityResDto applyActivity(ApplyApplicationReqDto request) {

        Activity activity = getActivity(request.getActivityId());

        OUser applyUser = getUser(request.getUserId());

        //이미 추가된 사람인지 체크
        if (crewService.existCrew(applyUser, activity)) {
            throw new DuplicatedResourceException("해당 활동에 이미 속해있습니다.");
        }

        activity.addParticipant();
        Crews crew = crewService.addCrew(request, activity, applyUser);

        return new ApplyActivityResDto(
                UUIDGenerator.changeUuidToString(activity.getUuid())
                , UUIDGenerator.changeUuidToString(crew.getUuid()));
    }

    private Activity getActivity(String activityId) {
        return activityRepository.findByUuid(UUIDGenerator.changeUuidFromString(activityId))
                .orElseThrow(() -> new IdNotFoundException("해당 활동이 존재하지 않습니다."));
    }

    @Transactional
    public void modifyActivity(String activityId, ModifyActivityReqDto request, OUser user) {
        log.info("registerActivity activity id : {}, request :{}", activityId, request);

        //요청한 사람이 만든 활동인지 확인
        //TODO: 인터셉터로 변환
        Activity activity = getActivity(activityId);
        ActivityDetail activityDetail = getActivityDetail(activity);
        OUser host = crewService.findOwner(activity);
        if (!user.equals(host)) {
            throw new IllegalRequestException("요청한 유저에게 활동 수정 권한이 없습니다.");
        }

        if (request.getTitle() != null)
            activity.setTitle(request.getTitle());
        if (request.getLocation() != null)
            activity.setLocation(request.getLocation().toEntity());
        if (request.getTransportation() != null)
            activityDetail.setTransportation(request.getTransportation());
        if (request.getGarbageCategory() != null)
            activity.setGarbageCategory(request.getGarbageCategory());
        if (request.getLocationTag() != null)
            activity.setLocationTag(request.getLocationTag());
        if (request.getRecruitStartAt() != null)
            activity.setRecruitStartAt(request.getRecruitStartAt());
        if (request.getRecruitEndAt() != null)
            activity.setRecruitEndAt(request.getRecruitEndAt());
        if (request.getStartAt() != null)
            activity.setStartAt(request.getStartAt());
        if (request.getThumbnailUrl() != null)
            activity.setThumbnail(request.getThumbnailUrl());
        if (request.getKeeperIntroduction() != null)
            activityDetail.setKeeperIntroduction(request.getKeeperIntroduction());
        if (request.getKeeperImageUrl() != null)
            activityDetail.setKeeperImage(request.getKeeperImageUrl());
        if (request.getActivityStory() != null)
            activityDetail.setActivityStory(request.getActivityStory());
        if (request.getStoryImageUrl() != null)
            activityDetail.setStoryImage(request.getStoryImageUrl());
        if (request.getQuota() != null) activity.setQuota(request.getQuota());
        if (request.getProgramDetails() != null)
            activityDetail.setProgramDetails(request.getProgramDetails());
        if (request.getPreparation() != null)
            activityDetail.setPreparation(request.getPreparation());
        if (request.getRewards() != null)
            activityDetail.setRewards(request.getRewards());
        if (request.getEtc() != null)
            activityDetail.setEtc(request.getEtc());

        activityRepository.save(activity);
        activityDetail.setActivity(activity);
        activityDetailRepository.save(activityDetail);
    }

    private ActivityDetail getActivityDetail(Activity activity) {
        return activityDetailRepository.findByActivity(activity)
                .orElseThrow(() -> new IdNotFoundException("해당 상세 내용이 존재하지 않습니다."));
    }

    @Transactional
    public void modifyApplication(String applicationId, ModifyApplicationReqDto request, OUser user) {
        Activity activity = getActivity(applicationId);

        Crews crew = crewService.findCrew(user, activity);
        if (request.getName() != null) crew.setName(request.getName());
        if (request.getEmail() != null) crew.setEmail(request.getEmail());
        if (request.getId1365() != null) crew.setId1365(request.getId1365());
        if (request.getQuestion() != null) crew.setQuestion(request.getQuestion());
        if (request.getPhoneNumber() != null) crew.setPhoneNumber(request.getPhoneNumber());
        if (request.getStartPoint() != null) crew.setStartPoint(request.getStartPoint());
        if (request.getDayOfBirth() != null) crew.setDayOfBirth(request.getDayOfBirth());

        crewService.save(crew);
    }

    @Transactional
    public ApplicationReqDto getLastApplication(OUser user) {
        return crewService.findApplication(user);
    }
}
