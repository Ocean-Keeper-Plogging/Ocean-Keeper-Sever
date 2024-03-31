package com.server.oceankeeper.domain.statistics.service;

import com.server.oceankeeper.domain.activity.dto.inner.RegisterActivityEventDto;
import com.server.oceankeeper.domain.statistics.dto.ActivityInfoResDto;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.domain.statistics.entity.ActivityInfo;
import com.server.oceankeeper.domain.statistics.repository.ActivityInfoRepository;
import com.server.oceankeeper.domain.user.dto.UserAndActivityDto;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.util.TokenUtil;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityInfoService {
    private final ActivityInfoRepository activityInfoRepository;
    private final TokenUtil tokenUtil;

    @EventListener
    public void handle(ActivityEvent event) {
        if (event.getEvent().equals(OceanKeeperEventType.ACTIVITY_REGISTRATION_EVENT)) {
            registerActivity(event);
            log.debug("activity registered");
        } else if (event.getEvent().equals(OceanKeeperEventType.ACTIVITY_NO_SHOW_EVENT)) {
            noShowActivity(event);
            log.debug("no show activity");
        } else if (event.getEvent().equals(OceanKeeperEventType.ACTIVITY_PARTICIPATION_EVENT)) {
            participateActivity(event);
            log.debug("participated");
        } else if (event.getEvent().equals(OceanKeeperEventType.USER_JOINED_EVENT)) {
            userJoined(event);
            log.debug("user joined");
        } else if (event.getEvent().equals(OceanKeeperEventType.USER_WITHDRAWAL_EVENT)) {
            userWithdrawn(event);
            log.debug("user withdrawn");
        } else if (event.getEvent().equals(OceanKeeperEventType.ACTIVITY_PARTICIPATION_CANCEL_EVENT)) {
            cancelEvent(event);
            log.debug("activity canceled");
        } else if (event.getEvent().equals(OceanKeeperEventType.ACTIVITY_REGISTRATION_CANCEL_EVENT)) {
            cancelCrewActivityEvent(event);
            log.debug("activity canceled");
        }
    }

    private void userWithdrawn(ActivityEvent event) {
        OUser user = (OUser) event.getObject();
        ActivityInfo info = getActivityInfo(user);
        info.reset();
    }

    private void cancelEvent(ActivityEvent event) {
        OUser user = (OUser) event.getObject();
        ActivityInfo info = getActivityInfo(user);
        info.subActivityCount();
    }

    private void cancelCrewActivityEvent(ActivityEvent event) {
        UserAndActivityDto dto = (UserAndActivityDto) event.getObject();
        ActivityInfo info = getActivityInfo(dto.getUser());
        info.addCancelCount();
        info.subHostingCount();

        for (OUser crew : dto.getCrews().getUser()) {
            info = getActivityInfo(crew);
            info.subActivityCount();
        }
    }

    private void userJoined(ActivityEvent event) {
        OUser user = (OUser) event.getObject();
        ActivityInfo info = ActivityInfo.builder()
                .user(user)
                .countActivity(0)
                .countNoShow(0)
                .countHosting(0)
                .countCancel(0)
                .build();
        activityInfoRepository.save(info);
    }

    private void participateActivity(ActivityEvent event) {
        OUser user = (OUser) event.getObject();
        ActivityInfo info = getActivityInfo(user);
        info.addActivityCount();
        activityInfoRepository.save(info);
    }

    private ActivityInfo getActivityInfo(OUser user) {
        return activityInfoRepository.findByUser(user)
                .orElseThrow(() -> new IdNotFoundException("해당 유저의 활동 정보가 존재하지 않습니다"));
    }

    private void noShowActivity(ActivityEvent event) {
        OUser user = (OUser) event.getObject();
        ActivityInfo info = getActivityInfo(user);
        info.addNoShowCount();
        activityInfoRepository.save(info);
    }

    private void registerActivity(ActivityEvent event) {
        RegisterActivityEventDto eventDto = (RegisterActivityEventDto) event.getObject();
        ActivityInfo info = getActivityInfo(eventDto.getHost());
        info.addHostingCount();
        activityInfoRepository.save(info);
    }

    @Transactional
    public ActivityInfoResDto getUserActivityInfo(String userId, HttpServletRequest servletRequest) {
        OUser user = tokenUtil.getUserFromHeader(servletRequest);
        if (!userId.equals(UUIDGenerator.changeUuidToString(user.getUuid()))) {
            throw new IllegalRequestException("현재 유저는 해당 유저의 활동 정보를 조회할 수 없습니다.");
        }
        ActivityInfo activityInfo = activityInfoRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저의 활동 참여 정보가 없습니다."));
        return new ActivityInfoResDto(activityInfo);
    }

    @Transactional
    public ActivityInfoResDto getUserActivityInfo(OUser user) {
        ActivityInfo activityInfo = activityInfoRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저의 활동 참여 정보가 없습니다."));
        return new ActivityInfoResDto(activityInfo);
    }
}
