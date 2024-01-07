package com.server.oceankeeper.domain.scheduler.service;


import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JobMaker {
    public JobDetail buildJobDetail(String activityId, OceanKeeperEventType eventType, Class<? extends Job> job) {
        return JobBuilder.newJob(job)
                .withIdentity(activityId, eventType.toString())
                .usingJobData("activityId", activityId)
                .build();
    }

    public Trigger buildTrigger(String activityId, Date date, OceanKeeperEventType eventType) {
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(activityId, eventType.toString()))
                .startAt(date)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();
        return trigger;
    }
}
