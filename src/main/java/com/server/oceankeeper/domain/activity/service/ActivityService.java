package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dao.*;
import com.server.oceankeeper.domain.activity.dto.CrewInfoDetailDto;
import com.server.oceankeeper.domain.activity.dto.CrewInfoFileDto;
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
import com.server.oceankeeper.util.TokenUtil;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
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
    public GetActivityResDto getActivities(String activityId, String status, LocationTag locationTag, GarbageCategory garbageCategory, Integer pageSize) {
        ActivityStatus activityStatus = ActivityStatus.getStatus(status);
        Slice<AllActivityDao> response = activityRepository.getAllActivities(
                activityId != null ? UUIDGenerator.changeUuidFromString(activityId) : null,
                activityStatus,
                locationTag,
                garbageCategory,
                LocalDateTime.now(),
                PageRequest.ofSize(pageSize != null ? pageSize : 1));

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
                r.getLocation())).collect(Collectors.toList());

        return new GetActivityResDto(activities,
                new GetActivityResDto.Meta(activities.size(), !response.hasNext()));
    }

    @Transactional
    public RegisterActivityResDto registerActivity(RegisterActivityReqDto request) {
        log.info("registerActivity request :{}", request);

        checkRecruitDay(request.getRecruitStartAt(), request.getRecruitEndAt(), request.getStartAt());

        OUser user = getUser(request.getUserId());

        Activity activity = request.toActivityEntity();
        activityRepository.save(activity);

        ActivityDetail activityDetail = request.toActivityDetailEntity();
        activityDetail.setActivity(activity);
        activityDetailRepository.save(activityDetail);

        crewService.addHost(activity, user);

        //TODO:활동 등록 기록은 활동이 끝난 다음에 처리
        //EventPublisher.emit(new ActivityEvent(this, user, OceanKeeperEventType.ACTIVITY_REGISTRATION_EVENT));

        return new RegisterActivityResDto(UUIDGenerator.changeUuidToString(activity.getUuid()));
    }

    private void checkRecruitDay(LocalDate recruitStartAt, LocalDate recruitEndAt, LocalDateTime startAt) {
        if (recruitStartAt.isAfter(recruitEndAt) || startAt.isBefore(recruitEndAt.atStartOfDay())) {
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

        //이미 추가된 사람인지 체크
        if (crewService.existCrew(applyUser, activity)) {
            throw new DuplicatedResourceException("해당 활동에 이미 속해있습니다.");
        }

        activity.addParticipant();
        Crews crew = crewService.addCrew(request, activity, applyUser);

        EventPublisher.emit(new ActivityEvent(this, applyUser, OceanKeeperEventType.ACTIVITY_PARTICIPATION_EVENT));

        return new ApplyActivityResDto(
                UUIDGenerator.changeUuidToString(activity.getUuid())
                , UUIDGenerator.changeUuidToString(crew.getUuid()));
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
    public void modifyApplication(String applicationId, ModifyApplicationReqDto request, HttpServletRequest servletRequest) {
        OUser user = tokenUtil.getUserFromHeader(servletRequest);
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
    public ApplicationDto getLastApplication(HttpServletRequest servletRequest) {
        OUser user = tokenUtil.getUserFromHeader(servletRequest);
        log.info("JBJB user:{},", user);
        return crewService.getApplicationDto(user);
    }

    @Transactional
    public void cancelApplication(String applicationId, HttpServletRequest servletRequest) {
        OUser user = tokenUtil.getUserFromHeader(servletRequest);
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
        Slice<ActivityDao> response = activityRepository.getMyActivities(
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
        OUser user = tokenUtil.getUserFromHeader(servletRequest);
        Activity activity = getActivity(activityId);
        Crews host = crewService.findApplication(user, activity);

        //해당 크루가 호스트인지 확인
        if (!host.getActivityRole().equals(CrewRole.HOST))
            throw new IllegalRequestException("해당 요청은 호스트만 가능합니다.");

        if (activity.getActivityStatus().equals(ActivityStatus.CANCEL))
            throw new DuplicatedResourceException("이미 취소된 활동입니다.");

        //해당 활동에 속한 모두에게 활동 취소됨을 알림
        crewService.findCrews(activity).forEach(crewService::resetCrewInfo);
        ActivityDetail activityDetail = getActivityDetail(activity);

        activityDetail.reset();
        activity.reset();
    }

    @Transactional
    public Crews findCrews(String applicationId) {
        return crewService.findCrews(applicationId);
    }

    @Transactional
    public HostActivityDto getHostActivityName(HttpServletRequest servletRequest) {
        OUser user = tokenUtil.getUserFromHeader(servletRequest);
        List<HostActivityDao> activities = activityRepository.getHostActivityNameFromUser(user);
        return new HostActivityDto(activities.stream().map(
                        d -> new HostActivityDto.HostActivityInnerDto(
                                UUIDGenerator.changeUuidToString(d.getUuid()),
                                d.getTitle()))
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
        return new CrewInfoDetailDto(activityId, data);
    }

    @Transactional
    public OUser findOwner(Activity activity) {
        return crewService.findOwner(activity);
    }

    @Transactional
    public CrewInfoFileDto getCrewInfoFile(String activityId, HttpServletRequest request) {
        synchronized (this) {
            Activity activity = validateHost(activityId, request);

            try {
                return makeExcelFile(activity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Activity validateHost(String activityId, HttpServletRequest request) {
        OUser user = tokenUtil.getUserFromHeader(request);
        //validation
        Activity activity = getActivity(activityId);
        OUser host = findOwner(activity);
        if (!user.equals(host)) {
            log.error("current user :{}\n host :{}", user, host);
            throw new IllegalRequestException("요청한 유저에게 활동 조회 권한이 없습니다.");
        }
        return activity;
    }

    private CrewInfoFileDto makeExcelFile(Activity activity) throws IOException {
        Workbook xWorkbook = new XSSFWorkbook(); //엑셀파일 생성
        Sheet xSheet = xWorkbook.createSheet("sheet1"); //시트 생성
        Row xRow = null; //행 객체 생성
        Cell xCell = null; //열 객체 생성

        initializeExcelFormat(xSheet);

        int count = 1;
        int row = 1;

        for (Crews application : crewService.findCrews(activity)) {
            if(application.getActivityRole().equals(CrewRole.HOST))
                continue;
            xRow = xSheet.createRow(row);
            xCell = xRow.createCell(0);
            xCell.setCellValue(count);
            xCell = xRow.createCell(1);
            xCell.setCellValue(getValue(application.getName()));
            xCell = xRow.createCell(2);
            xCell.setCellValue(getValue(application.getPhoneNumber()));
            xCell = xRow.createCell(3);
            xCell.setCellValue(getValue(application.getId1365()));
            xCell = xRow.createCell(4);
            xCell.setCellValue(getValue(application.getEmail()));
            xCell = xRow.createCell(5);
            xCell.setCellValue(getValue(application.getDayOfBirth()));

            row++;
            count++;
        }

        xRow = xSheet.getRow(xSheet.getFirstRowNum());
        Iterator<Cell> cellIterator = xRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            int columnIndex = cell.getColumnIndex();
            xSheet.autoSizeColumn(columnIndex);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xWorkbook.write(outputStream);


        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        outputStream.close();
        return new CrewInfoFileDto(resource);
    }

    private void initializeExcelFormat(Sheet xSheet) {
        Row xRow;
        Cell xCell;
        xRow = xSheet.createRow(0);

        xCell = xRow.createCell(0);
        xCell.setCellValue("No.");
        xCell = xRow.createCell(1);
        xCell.setCellValue("이름");
        xCell = xRow.createCell(2);
        xCell.setCellValue("연락처");
        xCell = xRow.createCell(3);
        xCell.setCellValue("1365 아이디");
        xCell = xRow.createCell(4);
        xCell.setCellValue("이메일");
        xCell = xRow.createCell(5);
        xCell.setCellValue("생년월일");
    }

    private String getValue(String data) {
        return data == null ? "" : data;
    }
}
