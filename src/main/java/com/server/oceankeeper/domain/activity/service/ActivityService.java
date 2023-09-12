package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dto.*;
import com.server.oceankeeper.domain.activity.dto.request.ApplyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.request.ModifyActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.request.ModifyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.request.RegisterActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.response.*;
import com.server.oceankeeper.domain.activity.entity.*;
import com.server.oceankeeper.domain.activity.repository.ActivityDetailRepository;
import com.server.oceankeeper.domain.activity.repository.ActivityRepository;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.crew.service.CrewService;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import com.server.oceankeeper.global.exception.DuplicatedResourceException;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    public List<MyScheduledActivityDto> getMyScheduleActivity(String id) {
        return getMyActivitiesLimit5(new MyActivityParam(LocalDate.now(), UUIDGenerator.changeUuidFromString(id), CrewStatus.IN_PROGRESS));
    }

    private List<MyScheduledActivityDto> getMyActivitiesLimit5(MyActivityParam param) {
        List<MyActivityDao> response = activityRepository.getMyActivitiesLimit5(param);
        log.info("findCrews result :{}", response);

        List<MyScheduledActivityDto> result = new ArrayList<>();
        for (MyActivityDao dao : response) {
            MyScheduledActivityDto myActivity = MyScheduledActivityDto.builder()
                    .id(UUIDGenerator.changeUuidToString(dao.getUuid()))
                    .dDay(calculateDDay(dao.getStartAt()))
                    .location(dao.getAddress())
                    .startDay(getStartDay(dao.getStartAt()))
                    .title(dao.getTitle())
                    .build();
            result.add(myActivity);
        }
        return result;
    }

    private String getStartDay(LocalDateTime date) {
        StringBuilder result = new StringBuilder();

        final String dayOfWeek = date.getDayOfWeek().name();
        final int day = date.getDayOfMonth();
        final int month = date.getMonth().getValue();
        final int hour = date.getHour();
        final int minute = date.getMinute();

        result.append(month).append(".");
        result.append(day);
        result.append("(").append(toKorean(dayOfWeek)).append(") ");
        result.append(hour).append("시");
        if (minute != 0) {
            result.append(minute).append("분");
        }
        result.append(" 시작");
        log.info("date {} => get start day {}", date, result);

        return result.toString();
    }

    private static final String MONDAY = "MONDAY";
    private static final String TUESDAY = "TUESDAY";
    private static final String WEDNESDAY = "WEDNESDAY";
    private static final String THURSDAY = "THURSDAY";
    private static final String FRIDAY = "FRIDAY";
    private static final String SATURDAY = "SATURDAY";
    private static final String SUNDAY = "SUNDAY";

    private String toKorean(String dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "월";
            case TUESDAY:
                return "화";
            case WEDNESDAY:
                return "수";
            case THURSDAY:
                return "목";
            case FRIDAY:
                return "금";
            case SATURDAY:
                return "토";
            case SUNDAY:
                return "일";
            default:
                return "";
        }
    }

    private int calculateDDay(LocalDateTime startAt) {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), startAt.toLocalDate());
    }

    @Transactional
    public GetActivityResDto getActivities(String activityId, String status, LocationTag locationTag, GarbageCategory garbageCategory, Integer pageSize) {
        ActivityStatus activityStatus = ActivityStatus.getStatus(status);
        Slice<AllActivityDao> response = activityRepository.getAllActivities(activityId != null ? UUIDGenerator.changeUuidFromString(activityId) : null,
                activityStatus, locationTag, garbageCategory, PageRequest.ofSize(pageSize != null ? pageSize : 1));
        log.debug("getActivities response :{}", response);
        List<AllActivityResDto> activities = response.stream().map(r -> new AllActivityResDto(
                UUIDGenerator.changeUuidToString(r.getActivityId()),
                r.getTitle(),
                r.getLocationTag(),
                r.getGarbageCategory(),
                r.getHostNickname(),
                r.getQuota(),
                r.getParticipants(),
                r.getActivityImageUrl(),
                r.getRecruitStartAt().toString(),
                r.getRecruitEndAt().toString(),
                getStartDay(r.getStartAt()),
                r.getLocation())).collect(Collectors.toList());
        log.debug("getActivities activities :{}", response);
        return new GetActivityResDto(activities,
                new GetActivityResDto.Meta(activities.size(), !response.hasNext()));

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

        EventPublisher.raise(new ActivityEvent(this, user, OceanKeeperEventType.ACTIVITY_REGISTRATION_CANCEL_EVENT));

        return new RegisterActivityResDto(UUIDGenerator.changeUuidToString(activity.getUuid()));
    }

    @Transactional
    public OUser getUser(String userId) {
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

        EventPublisher.raise(new ActivityEvent(this, applyUser, OceanKeeperEventType.ACTIVITY_PARTICIPATION_EVENT));

        return new ApplyActivityResDto(
                UUIDGenerator.changeUuidToString(activity.getUuid())
                , UUIDGenerator.changeUuidToString(crew.getUuid()));
    }

    public Activity getActivity(String activityId) {
        return activityRepository.findByUuid(UUIDGenerator.changeUuidFromString(activityId))
                .orElseThrow(() -> new IdNotFoundException("해당 활동이 존재하지 않습니다."));
    }

    @Transactional
    public void modifyActivity(String activityId, ModifyActivityReqDto request, OUser user) {
        log.info("modifyActivity activity id : {}, request :{}", activityId, request);

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

        Crews crew = crewService.findApplication(user, activity);
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

    @Transactional
    public void cancelApplication(String applicationId, OUser user) {
        Crews crew = crewService.findApplication(user, applicationId);

        Activity activity = crew.getActivity();
        if (activity == null) {
            throw new ResourceNotFoundException("해당 지원서로 인한 활동이 존재하지 않습니다.");
        }

        crewService.deleteCrew(user, crew);
        activity.removeParticipant();
        activityRepository.save(activity);
    }

    @Transactional
    public ActivityDetailResDto getActivityDetail(String activityId) {
        Activity activity = getActivity(activityId);
        ActivityDetail activityDetail = getActivityDetail(activity);
        OUser host = crewService.findOwner(activity);
        return new ActivityDetailResDto(activity, activityDetail, host);
    }

    @Transactional
    public ApplicationReqDto getApplication(String applicationId) {
        return crewService.findApplication(applicationId);
    }

    @Transactional
    public MyActivityDto getMyActivities(String userId, String activityId, String status, Integer pageSize) {
        CrewRole role = CrewRole.getRole(status);
        Slice<ActivityDao> response = activityRepository.getMyActivities(UUIDGenerator.changeUuidFromString(userId),
                activityId != null ? UUIDGenerator.changeUuidFromString(activityId) : null,
                status == null ? null : status.equals("closed") ? ActivityStatus.CLOSE : ActivityStatus.OPEN,
                role, PageRequest.ofSize(pageSize != null ? pageSize : 1));
        log.debug("getMyActivities response :{}", response);

        return new MyActivityDto(response.stream().map(r -> new MyActivityDto.MyActivityDetail(
                UUIDGenerator.changeUuidToString(r.getActivityId()),
                r.getTitle(),
                r.getHostNickname(),
                r.getQuota(),
                r.getParticipants(),
                r.getActivityImageUrl(),
                r.getRecruitStartAt(),
                r.getRecruitEndAt(),
                r.getStartAt(),
                r.getStatus(),
                r.getAddress())).collect(Collectors.toList()));
    }

    @Transactional
    public void cancelActivity(String activityId, OUser user) {
        Activity activity = getActivity(activityId);
        Crews host = crewService.findApplication(user, activity);

        //해당 크루가 호스트인지 확인
        if (!host.getActivityRole().equals(CrewRole.HOST))
            throw new IllegalRequestException("해당 요청은 호스트만 가능합니다.");

        //해당 활동에 속한 모두에게 활동 취소됨을 알림
        crewService.findCrews(activity).forEach(crewService::deleteByHost);
        activityRepository.delete(activity);
    }

    @Transactional
    public HostActivityDto getHostActivityName(OUser user) {
        List<HostActivityDao> activities = activityRepository.getHostActivityNameFromUser(user);
        return new HostActivityDto(activities.stream().map(
                        d -> new HostActivityDto.HostActivityInnerDto(
                                UUIDGenerator.changeUuidToString(d.getUuid()),
                                d.getTitle()))
                .collect(Collectors.toList()));
    }

    @Transactional
    public CrewActivityDto getCrewInfo(OUser user) {
        List<CrewInfoDao> activities = activityRepository.getCrewInfoFromHostUser(user);
        List<CrewActivityDto.CrewActivityInnerClass> data = activities.stream().map(
                d -> new CrewActivityDto.CrewActivityInnerClass(d.getNickname())).collect(Collectors.toList());
        if (!activities.isEmpty()) {
            return new CrewActivityDto(
                    UUIDGenerator.changeUuidToString(activities.get(0).getUuid()),
                    activities.get(0).getTitle(),
                    data);
        }
        return new CrewActivityDto(
                "",
                "",
                null);
    }
}
