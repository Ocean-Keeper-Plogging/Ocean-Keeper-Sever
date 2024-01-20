package com.server.oceankeeper.domain.scheduler.service;

import com.server.oceankeeper.domain.activity.dto.inner.RegisterActivityEventDto;
import com.server.oceankeeper.domain.scheduler.job.ActivityEndJob;
import com.server.oceankeeper.domain.scheduler.job.ActivityStarterJob;
import com.server.oceankeeper.domain.scheduler.job.RecruitmentEndJob;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {
    private final Scheduler scheduler;
    private final JobMaker jobMaker;
    private final EventPublisher publisher;

    @Value("${fcm.time}")
    private Integer activityStarterTime = 3600; //default 1 hour

    @PostConstruct
    void init() {
        try {
            scheduler.clear();
            //scheduler.start();
            log.debug("scheduler started");
            log.debug("fcm time : {}", activityStarterTime);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteCrewData() {
        publisher.emit(new ActivityEvent(this, null, OceanKeeperEventType.ACTIVITY_INFO_DELETE_EVENT));
    }


    @EventListener
    public void handleEvent(ActivityEvent event) {
        if (event.getEvent().equals(OceanKeeperEventType.ACTIVITY_REGISTRATION_EVENT)) {
            handleActivityStartSoonEvent(event);
        } else if (event.getEvent().equals(OceanKeeperEventType.ACTIVITY_CHANGED_EVENT)) {
            handleActivityModificationEvent(event);
        } else if (event.getEvent().equals(OceanKeeperEventType.ACTIVITY_RECRUITMENT_CLOSED_EVENT)) {
            handleActivityRecruitmentEndEvent(event);
        }
    }

    private void handleActivityStartSoonEvent(ActivityEvent event) {
        log.debug("활동 곧 시작 이벤트 등록 event:{}", event);

        //활동 곧 시작 이벤트 시각
        RegisterActivityEventDto eventDto = (RegisterActivityEventDto) event.getObject();

        LocalDateTime activityStartAt = setActivityStarterTime(eventDto);
        Date date = Date.from(activityStartAt.atZone(ZoneId.systemDefault()).toInstant());

        TimeZone tz = Calendar.getInstance().getTimeZone();
        log.debug("JBJB 활동 곧 시작 이벤트 등록 tz : {}, id:{}, date:{}", tz.getDisplayName(), tz.getID(), date);

        registerSchedule(eventDto.getActivityId(), date, OceanKeeperEventType.ACTIVITY_START_SOON_EVENT, ActivityStarterJob.class);
    }

    private void handleActivityRecruitmentEndEvent(ActivityEvent event) {
        log.debug("활동 모집 종료 이벤트 등록 event:{}", event);

        RegisterActivityEventDto eventDto = (RegisterActivityEventDto) event.getObject();

        LocalDateTime recruitmentEndTime = eventDto.getRecruitEndAt().plusDays(1).atStartOfDay();
        Date recruitmentEndDate = Date.from(recruitmentEndTime.atZone(ZoneId.systemDefault()).toInstant());

        TimeZone tz = Calendar.getInstance().getTimeZone();
        log.debug("JBJB 활동 모집 종료 이벤트 등록 tz : {}, id:{}, date:{}", tz.getDisplayName(), tz.getID(), recruitmentEndDate);

        //활동 모집종료 스케줄링 등록, 활동 종료 스케줄링 등록
        registerSchedule(eventDto.getActivityId(), recruitmentEndDate, event.getEvent(), RecruitmentEndJob.class);
        registerActivityCloseSchedule(event);
    }

    private void registerActivityCloseSchedule(ActivityEvent event) {
        log.debug("활동 종료 이벤트 등록 event:{}", event);
        RegisterActivityEventDto eventDto = (RegisterActivityEventDto) event.getObject();
        LocalDateTime activityEndTime = eventDto.getStartAt().plusDays(1).with(LocalTime.MIDNIGHT);
        Date activityEndDate = Date.from(activityEndTime.atZone(ZoneId.systemDefault()).toInstant());

        TimeZone tz = Calendar.getInstance().getTimeZone();
        log.debug("JBJB 활동 종료 이벤트 등록 tz : {}, id:{}, date:{}", tz.getDisplayName(), tz.getID(), activityEndDate);

        registerSchedule(eventDto.getActivityId(), activityEndDate, OceanKeeperEventType.ACTIVITY_CLOSE_EVENT, ActivityEndJob.class);
    }

    private void handleActivityModificationEvent(ActivityEvent event) {
        log.debug("활동 수정 이벤트 등록 event:{}", event);

        RegisterActivityEventDto eventDto = (RegisterActivityEventDto) event.getObject();

        LocalDateTime activityStartAt = setActivityStarterTime(eventDto);
        Date activityStartDate = Date.from(activityStartAt.atZone(ZoneId.systemDefault()).toInstant());
        Date recruitmentEndDate = Date.from(eventDto.getRecruitEndAt().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        try {
            //활동 1시간전 알림,활동 모집종료 알림,활동 종료 알림 재등록
            reRegisterScheduler(event, activityStartDate, recruitmentEndDate);
        } catch (SchedulerException e) {
            throw new RuntimeException("활동 수정 이벤트 fcm 등록 스케줄러 에러. 관리자 문의 필요");
        }
    }

    private void reRegisterScheduler(ActivityEvent event, Date activityStartDate, Date recruitmentEnd) throws SchedulerException {
        RegisterActivityEventDto eventDto = (RegisterActivityEventDto) event.getObject();
        deleteSchedule(eventDto.getActivityId(), OceanKeeperEventType.ACTIVITY_START_SOON_EVENT);
        deleteSchedule(eventDto.getActivityId(), OceanKeeperEventType.ACTIVITY_RECRUITMENT_CLOSED_EVENT);
        deleteSchedule(eventDto.getActivityId(), OceanKeeperEventType.ACTIVITY_CLOSE_EVENT);

        registerSchedule(eventDto.getActivityId(),
                activityStartDate, OceanKeeperEventType.ACTIVITY_START_SOON_EVENT, ActivityStarterJob.class);
        registerSchedule(eventDto.getActivityId(),
                recruitmentEnd, OceanKeeperEventType.ACTIVITY_RECRUITMENT_CLOSED_EVENT, RecruitmentEndJob.class);

        registerActivityCloseSchedule(event);
    }

    private LocalDateTime setActivityStarterTime(RegisterActivityEventDto eventDto) {
        return eventDto.getStartAt().minusSeconds(activityStarterTime);
    }

    //TODO: fallback
    private void registerSchedule(String activityId, Date date, OceanKeeperEventType eventType, Class<? extends Job> job) {
        JobDetail jobDetail = jobMaker.buildJobDetail(activityId, eventType, job);
        Trigger trigger = jobMaker.buildTrigger(activityId, date, eventType);
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("scheduling error e:{}", e.toString());
            throw new RuntimeException(e);
        }
    }

    private void deleteSchedule(String activityId, OceanKeeperEventType eventType) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(activityId, eventType.toString());
        if (scheduler.checkExists(triggerKey)) {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            //scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(trigger.getJobKey());
            log.info("[deleteSchedule] activity id:{}, event type:{}", activityId, eventType.getValue());
        }
    }
}
