package com.server.oceankeeper.domain.scheduler.job;

import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.message.entity.MessageEvent;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecruitmentEndJob implements Job {
    @Autowired
    private ActivityService activityService;

    @Override
    public void execute(JobExecutionContext context) {
        String activityId = context.getJobDetail().getJobDataMap().getString("activityId");

        log.debug("활동 모집 종료 job scheduling success. activity id :{}", activityId);
        activityService.finishRecruitment(activityId);
    }
}