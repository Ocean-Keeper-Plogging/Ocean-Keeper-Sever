package com.server.oceankeeper.domain.scheduler.job;

import com.server.oceankeeper.domain.activity.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Component
public class ActivityStarterJob implements Job {
    @Autowired
    private ActivityService activityService;

    @Override
    public void execute(JobExecutionContext context) {
        String activityId = context.getJobDetail().getJobDataMap().getString("activityId");
        activityService.startActivitySoon(activityId);

        log.debug("활동 시작 예정 job scheduling success. activity id :{}", activityId);
    }
}