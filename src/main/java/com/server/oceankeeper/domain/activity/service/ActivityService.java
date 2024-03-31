package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dao.*;
import com.server.oceankeeper.domain.activity.dto.inner.RegisterActivityEventDto;
import com.server.oceankeeper.domain.activity.dto.inner.UserListDto;
import com.server.oceankeeper.domain.activity.dto.request.ApplyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.request.ModifyActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.request.ModifyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.request.RegisterActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.response.*;
import com.server.oceankeeper.domain.activity.entity.*;
import com.server.oceankeeper.domain.activity.repository.ActivityDetailRepository;
import com.server.oceankeeper.domain.activity.repository.ActivityRepository;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.entity.Crews;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.crew.service.CrewService;
import com.server.oceankeeper.domain.message.entity.MessageEvent;
import com.server.oceankeeper.domain.statistics.dto.ActivityInfoResDto;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.domain.user.dto.UserAndActivityDto;
import com.server.oceankeeper.domain.user.dto.UserInfoDto;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import com.server.oceankeeper.global.exception.DuplicatedResourceException;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.util.TokenUtil;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.server.oceankeeper.domain.activity.entity.ActivityStatus.getActivityStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityDetailRepository activityDetailRepository;
    private final UserRepository userRepository;
    private final CrewService crewService;
    private final TokenUtil tokenUtil;
    private final ExcelMaker excelMaker;
    private final EventPublisher publisher;

    @Transactional
    public List<MyScheduledActivityDto> getMyScheduleActivity(String id) {
        return getMyActivitiesLimit5(new MyActivityParam(LocalDateTime.now(), UUIDGenerator.changeUuidFromString(id)));
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
        //log.info("date {} => get start day {}", date, result);

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
    public GetActivityResDto getActivities(String activityId, String status,
                                           LocationTag locationTag, GarbageCategory garbageCategory,
                                           Integer pageSize, HttpServletRequest request) {
        OUser requester = tokenUtil.getUserFromHeader(request);
        ActivityStatus activityStatus = ActivityStatus.getStatus(status);
        Slice<AllActivityDao> response = activityRepository.getAllActivities(
                activityId != null ? UUIDGenerator.changeUuidFromString(activityId) : null,
                activityStatus,
                locationTag,
                garbageCategory,
                PageRequest.ofSize(pageSize != null ? pageSize : 1),
                requester);

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
                getActivityStatus(r.getRecruitEndAt(), r.getStartAt()),
                r.getLocation(),
                r.getRewards(),
                checkRecruitmentStarted(r.getRecruitStartAt(), r.getRecruitEndAt()))).collect(Collectors.toList());

        return new GetActivityResDto(activities,
                new GetActivityResDto.Meta(activities.size(), !response.hasNext()));
    }

    private boolean checkRecruitmentStarted(LocalDate recruitStartAt, LocalDate recruitEndAt) {
        LocalDate current = LocalDate.now();
        return (current.isEqual(recruitStartAt) || current.isAfter(recruitStartAt))
                && (current.isEqual(recruitEndAt) || current.isBefore(recruitEndAt));
    }

    @Transactional
    public RegisterActivityResDto registerActivity(RegisterActivityReqDto request) {
        log.info("registerActivity request :{}", request);

        checkRecruitDay(request.getRecruitStartAt(), request.getRecruitEndAt(), request.getStartAt());

        OUser user = getUser(request.getUserId());

        Activity activity = request.toActivityEntity();
        activity.setHost(user);

        activityRepository.save(activity);

        ActivityDetail activityDetail = request.toActivityDetailEntity();
        activityDetail.setActivity(activity);
        activityDetailRepository.save(activityDetail);

        crewService.addHost(activity, user);

        publisher.emit(new ActivityEvent(
                this,
                new RegisterActivityEventDto(activity.getStartAt(), activity.getRecruitEndAt(), UUIDGenerator.changeUuidToString(activity.getUuid()), user),
                OceanKeeperEventType.ACTIVITY_REGISTRATION_EVENT));

        publisher.emit(new ActivityEvent(
                this,
                new RegisterActivityEventDto(activity.getStartAt(), activity.getRecruitEndAt(), UUIDGenerator.changeUuidToString(activity.getUuid()), user),
                OceanKeeperEventType.ACTIVITY_RECRUITMENT_CLOSED_EVENT));

        return new RegisterActivityResDto(UUIDGenerator.changeUuidToString(activity.getUuid()));
    }

    private void checkRecruitDay(LocalDate recruitStartAt, LocalDate recruitEndAt, LocalDateTime startAt) {
        if (recruitStartAt.isAfter(recruitEndAt) || startAt.isBefore(recruitEndAt.plusDays(1).atStartOfDay().minusSeconds(1)) ||
                startAt.isBefore(LocalDateTime.now())) {
            throw new IllegalRequestException("모집 기간과 활동 시작 시각을 확인해주세요");
        }
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
        log.debug("apply user : {}", applyUser);

        //이미 추가된 사람인지 체크
        if (crewService.existCrew(applyUser, activity)) {
            throw new DuplicatedResourceException("해당 활동에 이미 속해있습니다.");
        }

        activity.addParticipant();
        OUser host = findOwner(activity);
        Crews crew = crewService.addCrew(request, activity, applyUser, host);

        publisher.emit(new ActivityEvent(this, applyUser, OceanKeeperEventType.ACTIVITY_PARTICIPATION_EVENT));

        return new ApplyActivityResDto(
                UUIDGenerator.changeUuidToString(activity.getUuid())
                , UUIDGenerator.changeUuidToString(crew.getUuid()));
    }

    @Transactional
    public OUser findOwner(Activity activity) {
        return activity.getHost();
    }

    public Activity getActivity(String activityId) {
        return activityRepository.findByUuid(UUIDGenerator.changeUuidFromString(activityId))
                .orElseThrow(() -> new ResourceNotFoundException("해당 활동이 존재하지 않습니다."));
    }

    @Transactional
    public void modifyActivity(String activityId, ModifyActivityReqDto request, HttpServletRequest servletRequest) {
        log.debug("modifyActivity activity id : {}, request :{}", activityId, request);
        OUser user = tokenUtil.getUserFromHeader(servletRequest);

        //요청한 사람이 만든 활동인지 확인
        //TODO: 인터셉터로 변환
        Activity activity = getActivity(activityId);
        ActivityDetail activityDetail = getActivityDetail(activity);
        OUser host = findOwner(activity);
        if (!user.equals(host)) {
            log.error("current user :{}\n host :{}", user, host);
            throw new IllegalRequestException("요청한 유저에게 활동 수정 권한이 없습니다.");
        }

        boolean activityStartTimeChanged = false;

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
        if (request.getRecruitStartAt() != null) {
            checkRecruitDay(
                    request.getRecruitStartAt(),
                    request.getRecruitEndAt() != null ? request.getRecruitEndAt() : activity.getRecruitEndAt(),
                    request.getStartAt() != null ? request.getStartAt() : activity.getStartAt());
            activity.setRecruitStartAt(request.getRecruitStartAt());
        }
        if (request.getRecruitEndAt() != null) {
            checkRecruitDay(
                    request.getRecruitStartAt() != null ? request.getRecruitStartAt() : activity.getRecruitStartAt(),
                    request.getRecruitEndAt(),
                    request.getStartAt() != null ? request.getStartAt() : activity.getStartAt());
            activity.setRecruitEndAt(request.getRecruitEndAt());
        }
        if (request.getStartAt() != null) {
            checkRecruitDay(
                    request.getRecruitStartAt() != null ? request.getRecruitStartAt() : activity.getRecruitStartAt(),
                    request.getRecruitEndAt() != null ? request.getRecruitEndAt() : activity.getRecruitEndAt(),
                    request.getStartAt());
            activity.setStartAt(request.getStartAt());
            activityStartTimeChanged = true;
        }
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
            activity.setRewards(request.getRewards());
        if (request.getEtc() != null)
            activityDetail.setEtc(request.getEtc());

        activityRepository.save(activity);
        activityDetail.setActivity(activity);
        activityDetailRepository.save(activityDetail);

        if (activityStartTimeChanged) {
            publisher.emit(new ActivityEvent(
                    this,
                    new RegisterActivityEventDto(activity.getStartAt(), activity.getRecruitEndAt(), UUIDGenerator.changeUuidToString(activity.getUuid()), null),
                    OceanKeeperEventType.ACTIVITY_CHANGED_EVENT));

            publisher.emit(new MessageEvent(
                    this,
                    getUserListDtoByActivityId(activityId, CrewRole.CREW),
                    OceanKeeperEventType.ACTIVITY_CHANGED_EVENT));
        }
    }

    private ActivityDetail getActivityDetail(Activity activity) {
        return activityDetailRepository.findByActivity(activity)
                .orElseThrow(() -> new IdNotFoundException("해당 상세 내용이 존재하지 않습니다."));
    }

    @Transactional
    public void modifyApplication(String applicationId, ModifyApplicationReqDto request, HttpServletRequest servletRequest) {
        OUser user = tokenUtil.getUserFromHeader(servletRequest);

        Crews crew = crewService.findApplication(user, applicationId);
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
    public ApplicationDto getLastApplication(HttpServletRequest servletRequest) {
        OUser user = tokenUtil.getUserFromHeader(servletRequest);
        return crewService.getNotEmptyApplicationDto(user);
    }

    @Transactional
    public void cancelApplication(String applicationId, HttpServletRequest servletRequest) {
        OUser user = tokenUtil.getUserFromHeader(servletRequest);
        cancelApplication(applicationId, user);
    }

    private void cancelApplication(String applicationId, OUser user) {
        log.info("[cancelApplication] applicationId:{}, user:{}", applicationId, user);
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
        OUser host = findOwner(activity);
        return new ActivityDetailResDto(activity, activityDetail, host);
    }

    @Transactional
    public ApplicationDto getApplication(String applicationId) {
        return crewService.getApplicationDto(applicationId);
    }

    @Transactional
    public Slice<ActivityDao> getActivityDao(String userId, String activityId, String status, String roleStr, Integer pageSize) {
        CrewRole role = CrewRole.getRole(roleStr);
        ActivityStatus activityStatus = ActivityStatus.getStatus(status);
        Slice<ActivityDao> response = activityRepository.getMyActivitiesWithoutCancel(
                UUIDGenerator.changeUuidFromString(userId),
                activityId != null ? UUIDGenerator.changeUuidFromString(activityId) : null,
                activityStatus,
                role,
                LocalDateTime.now(),
                PageRequest.ofSize(pageSize != null ? pageSize : 5));
        return response;
    }

    public CrewStatus getCrewStatusFromActivityDao(ActivityDao r) {
        final CrewStatus status = r.getCrewStatus();
        return (status == CrewStatus.REJECT || status == CrewStatus.NO_SHOW || status == CrewStatus.CANCEL) ? status :
                LocalDateTime.now().isBefore(r.getStartAt()) ? CrewStatus.IN_PROGRESS : CrewStatus.CLOSED;
    }

    @Transactional
    public void cancelActivity(String activityId, HttpServletRequest servletRequest) {
        log.info("[cancelActivity] activityId: {}", activityId);
        OUser hostUser = tokenUtil.getUserFromHeader(servletRequest);
        cancelActivity(activityId, hostUser);
    }

    private void cancelActivity(String activityId, OUser hostUser) {
        log.info("[cancelActivity] activityId:{}, user:{}", activityId, hostUser);
        Activity activity = getActivity(activityId);
        Crews host = crewService.findApplication(hostUser, activity);

        //해당 크루가 호스트인지 확인
        if (!host.getActivityRole().equals(CrewRole.HOST))
            throw new IllegalRequestException("해당 요청은 호스트만 가능합니다.");

        if (activity.getActivityStatus().equals(ActivityStatus.CANCEL))
            throw new DuplicatedResourceException("이미 취소된 활동입니다.");

        ActivityDetail activityDetail = getActivityDetail(activity);

        activityDetail.reset();
        activity.reset();

        //해당 활동에 속한 크루원에게 활동 취소됨을 알림
        List<Crews> crews = crewService.findCrews(activity);
        for (Crews crew : crews) {
            crew.reset();
        }
        UserListDto dto = new UserListDto(crews.stream().filter(c -> c.getActivityRole().equals(CrewRole.CREW))
                .map(Crews::getUser).collect(Collectors.toList()));
        //Notify crews to be canceled activity
        publisher.emit(new ActivityEvent(this, new UserAndActivityDto(hostUser, activity, dto), OceanKeeperEventType.ACTIVITY_REGISTRATION_CANCEL_EVENT));
        publisher.emit(new MessageEvent(this, dto, OceanKeeperEventType.ACTIVITY_REGISTRATION_CANCEL_EVENT));
    }

    @Transactional
    public Crews findCrew(String applicationId) {
        return crewService.findCrews(applicationId);
    }

    @Transactional
    public HostActivityDto getHostActivityName(HttpServletRequest servletRequest) {
        OUser host = tokenUtil.getUserFromHeader(servletRequest);
        List<Activity> activities = activityRepository.findByHost(host);
        return new HostActivityDto(activities.stream().map(
                        activity -> new HostActivityDto.HostActivityInnerDto(
                                UUIDGenerator.changeUuidToString(activity.getUuid()),
                                activity.getTitle()))
                .collect(Collectors.toList()));
    }

    @Transactional
    public CrewActivityDto getCrewInfo(String activityId, HttpServletRequest servletRequest) {
        OUser user = tokenUtil.getUserFromHeader(servletRequest);

        List<CrewInfoDao> crewInfo = activityRepository.getCrewInfoFromHostUser(user, UUIDGenerator.changeUuidFromString(activityId));
        List<CrewActivityDto.CrewActivityInnerClass> data = crewInfo.stream().map(
                d -> new CrewActivityDto.CrewActivityInnerClass(d.getNickname())).collect(Collectors.toList());
        if (!crewInfo.isEmpty()) {
            return new CrewActivityDto(
                    UUIDGenerator.changeUuidToString(crewInfo.get(0).getUuid()),
                    crewInfo.get(0).getTitle(),
                    data);
        }
        return new CrewActivityDto(
                "",
                "",
                null);
    }

    @Transactional
    public CrewInfoDetailDto getCrewInfoDetail(String activityId, HttpServletRequest servletRequest) {
        validateHost(activityId, servletRequest);

        List<CrewInfoDetailDao> crewInfo = activityRepository.getCrewInfo(UUIDGenerator.changeUuidFromString(activityId));
        List<CrewInfoDetailDto.CrewInfoDetailData> data = IntStream.range(0, crewInfo.size())
                .mapToObj(i -> new CrewInfoDetailDto.CrewInfoDetailData(i + 1, crewInfo.get(i)))
                .collect(Collectors.toList());
        log.debug("crew detail info :{}", data);
        if (!crewInfo.isEmpty())
            return new CrewInfoDetailDto(new CrewInfoDetailDto.ActivityInfo(activityId, crewInfo.get(0).getActivityStatus()), data);

        return new CrewInfoDetailDto(new CrewInfoDetailDto.ActivityInfo(activityId, null), data);
    }

    @Transactional
    //public CrewInfoFileDto getCrewInfoFile(String activityId, HttpServletRequest request) {
    public void getCrewInfoFile(String activityId, HttpServletRequest request, HttpServletResponse response) {
        synchronized (this) {
            Activity activity = validateHost(activityId, request);
            ActivityStatus activityStatus = getActivityStatus(activity.getRecruitEndAt(), activity.getStartAt());

            if (!(activityStatus.equals(ActivityStatus.RECRUITMENT_CLOSE)
                    || activityStatus.equals(ActivityStatus.CLOSED)))
                throw new IllegalRequestException("모집 종료 이후 엑셀 다운로드 가능합니다.");

            try {
                List<Crews> crews = crewService.findCrews(activity);
                excelMaker.makeExcelFile(crews, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Transactional
    public void startActivitySoon(String activityId) {
        log.info("[startActivitySoon] activity id:{}", activityId);
        UserListDto dto = getUserListDtoByActivityId(activityId, CrewRole.CREW);
        publisher.emit(new MessageEvent(this, dto, OceanKeeperEventType.ACTIVITY_START_SOON_EVENT));
    }

    private UserListDto getUserListDtoByActivityId(String activityId, CrewRole crewRole) {
        List<CrewDeviceTokensDao> result =
                activityRepository.getUserFromActivityId(UUIDGenerator.changeUuidFromString(activityId), crewRole);
        return new UserListDto(result.stream().map(CrewDeviceTokensDao::getUser).collect(Collectors.toList()));
    }

    @Transactional
    public void finishRecruitment(String activityId) {
        log.info("[finishRecruitment] activity id:{}", activityId);

        Activity activity = getActivity(activityId);
        if (activity.getActivityStatus().equals(ActivityStatus.OPEN))
            activity.closeRecruitment();
        else
            throw new RuntimeException("모집중이지 않은 활동");

        List<Crews> crews = crewService.findCrews(activity);
        sendActivityRecruitmentCloseMessage(crews);
    }

    @Transactional
    public void testCloseActivity() {
        List<Activity> activities = activityRepository.findAll();
        for (Activity activity : activities) {
            ActivityStatus status = activity.getActivityStatus();
            List<Crews> crews = crewService.findCrews(activity);
            if (status.equals(ActivityStatus.CLOSED)) {
                for (Crews crew : crews)
                    if (!(crew.getCrewStatus().equals(CrewStatus.REJECT)
                            || crew.getCrewStatus().equals(CrewStatus.CANCEL)
                            || crew.getCrewStatus().equals(CrewStatus.NO_SHOW)))
                        crew.closeApplication();
            }
        }
    }

    @Transactional
    public void closeActivity(String activityId) {
        log.info("[closeActivity] activity id:{}", activityId);

        Activity activity = getActivity(activityId);
        if (activity.getActivityStatus().equals(ActivityStatus.RECRUITMENT_CLOSE))
            activity.closeActivity();
        else
            throw new RuntimeException("모집 종료되지 않은 활동");

        List<Crews> crews = crewService.findCrews(activity);
        for (Crews crew : crews)
            crew.closeApplication();

        //Send activity close event fcm
        //sendActivityCloseMessage(crews);
    }

    //TODO: If needed, use it
//    private void sendActivityMessage(List<Crews> crews, OceanKeeperEventType eventType) {
//        UserListDto dto = new UserListDto(crews.stream().map(Crews::getUser).collect(Collectors.toList()));
//        publisher.emit(new MessageEvent(this, dto, eventType));
//    }
//
//    private void sendActivityCloseMessage(List<Crews> crews) {
//        sendActivityMessage(crews, OceanKeeperEventType.ACTIVITY_CLOSE_EVENT);
//    }

    private void sendActivityRecruitmentCloseMessage(List<Crews> crews) {
        UserListDto dto = new UserListDto(crews.stream().filter(c -> c.getActivityRole().equals(CrewRole.CREW))
                .map(Crews::getUser).collect(Collectors.toList()));
        publisher.emit(new MessageEvent(this, dto, OceanKeeperEventType.ACTIVITY_RECRUITMENT_CLOSED_EVENT));
    }

    private Activity validateHost(String activityId, HttpServletRequest request) {
        OUser user = tokenUtil.getUserFromHeader(request);

        Activity activity = getActivity(activityId);
        OUser host = findOwner(activity);
        if (!user.equals(host)) {
            log.error("current user :{}\n host :{}", user, host);
            throw new IllegalRequestException("요청한 유저에게 활동 조회 권한이 없습니다.");
        }
        return activity;
    }

    @EventListener
    @Transactional
    public void handleEvent(ActivityEvent event) {
        if (event.getEvent().equals(OceanKeeperEventType.ACTIVITY_INFO_DELETE_EVENT)) {
            handleActivityInfoDeleteEvent();
        }
        if (event.getEvent().equals(OceanKeeperEventType.USER_WITHDRAWAL_EVENT)) {
            handleUserWithdrawnEvent(event);
        }
    }

    @Transactional
    public void handleUserWithdrawnEvent(ActivityEvent event) {
        OUser user = (OUser) event.getObject();
        List<Crews> participatedApplicationInfo = crewService.findUserCrewInfo(user);
        for (Crews application : participatedApplicationInfo) {
            if (application.getHost().equals(user))
                cancelActivity(UUIDGenerator.changeUuidToString(application.getActivity().getUuid()), user);
            else
                cancelApplication(UUIDGenerator.changeUuidToString(application.getUuid()), user);
        }
    }

    @Transactional
    public void handleActivityInfoDeleteEvent() {
        long result = activityRepository.selectByCrewStatusAndStartAtAndUpdateCrewStatusAsDeleted(CrewStatus.CLOSED, 14);
        if (result != 0) {
            log.debug("14일 지난 활동 신청서 삭제 성공 count:{}", result);
        } else {
            log.debug("14일 지난 활동 신청서 삭제 없음");
        }
    }

    @Transactional
    public FullApplicationResDto getFullApplication(String applicationId, HttpServletRequest request) {
        FullApplicationDao queryResult = crewService.getFullApplication(applicationId);

        //Validator
        Long hostId = queryResult.getHostId();
        OUser requester = tokenUtil.getUserFromHeader(request);

        if (!hostId.equals(requester.getId())) {
            log.error("current user :{}\n host :{}", requester, hostId);
            throw new IllegalRequestException("요청한 유저에게는 신청서 읽기 권한이 없습니다.");
        }
        //Validator end

        UserInfoDto userInfo = UserInfoDto.builder()
                .nickname(queryResult.getNickname())
                .profile(queryResult.getProfileUrl())
                .build();

        ApplicationDto applicationDto = ApplicationDto.builder()
                .name(queryResult.getCrewName())
                .id1365(queryResult.getId1365())
                .phoneNumber(queryResult.getPhoneNumber())
                .question(queryResult.getQuestion())
                .transportation(queryResult.getTransportation())
                .startPoint(queryResult.getStartPoint())
                .email(queryResult.getEmail())
                .build();

        ActivityInfoResDto activityInfo = new ActivityInfoResDto(
                queryResult.getCountActivity(),
                queryResult.getCountHosting(),
                queryResult.getCountNoShow());

        ActivityTransportationDto activityTransportationDto = new ActivityTransportationDto(queryResult.getSupportedTransportation());

        return new FullApplicationResDto(userInfo, applicationDto, activityInfo, activityTransportationDto);
    }

    @Transactional
    public void reCalculate() {
        log.debug("JBJB recalculate dates");
        List<Activity> activities = activityRepository.findAll();
        for (Activity activity : activities) {
            if (activity.getActivityStatus().equals(ActivityStatus.CANCEL))
                continue;
            ActivityStatus newStatus = getActivityStatus(activity.getRecruitEndAt(), activity.getStartAt());
            if (newStatus.equals(ActivityStatus.OPEN))
                activity.setActivityStatus(ActivityStatus.OPEN);
            else if (newStatus.equals(ActivityStatus.RECRUITMENT_CLOSE))
                activity.closeRecruitment();
            else if (newStatus.equals(ActivityStatus.CLOSED))
                activity.closeActivity();
        }
    }
}
