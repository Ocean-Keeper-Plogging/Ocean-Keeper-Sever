package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dao.ActivityDao;
import com.server.oceankeeper.domain.activity.dto.request.ApplicationSettingReqDto;
import com.server.oceankeeper.domain.activity.dto.response.ApplicationSettingResDto;
import com.server.oceankeeper.domain.activity.dto.response.MyActivityDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.entity.Crews;
import com.server.oceankeeper.domain.message.dto.MessageDao;
import com.server.oceankeeper.domain.message.dto.request.MessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.response.MessageSendResDto;
import com.server.oceankeeper.domain.message.entity.MessageSentType;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.service.MessageService;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.util.TokenUtil;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.server.oceankeeper.domain.activity.entity.ActivityStatus.getActivityStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageFacadeService {
    private final ActivityService activityService;
    private final MessageService messageService;
    private final TokenUtil tokenUtil;
    private final EventPublisher publisher;

    @Transactional
    public MyActivityDto getMyActivities(String userId, String activityId, String status, String roleStr, Integer pageSize) {
        log.debug("[getMyActivities] user ID:{}, activity ID: {}, status:{}, role :{}", userId, activityId, status, roleStr);
        Slice<ActivityDao> activityDaoList = activityService.getActivityDao(userId, activityId, status, roleStr, pageSize);
        List<ActivityDao> activityDao = activityDaoList.getContent().stream()
                .filter(r -> r.getCrewStatus().equals(CrewStatus.REJECT)).collect(Collectors.toList());

        List<MessageDao> messageDaoList = new ArrayList<>();
        for (ActivityDao activity : activityDao) {
            List<MessageDao> messageDao = messageService.getMessageDao(null, null, userId, activity.getActivityId(), MessageType.REJECT).getContent();
            if (messageDao.size() == 1)
                messageDaoList.add(messageDao.get(0));
            else if (messageDao.size() >= 2) {
                log.error("messageDao size :{}. only include first message\n result :{}", messageDao.size(), messageDao);
                messageDaoList.add(messageDao.get(0));
            } else {
                log.error("messageDao empty");
            }
        }

        List<MyActivityDto.MyActivityDetail> response = new ArrayList<>();
        for (ActivityDao r : activityDaoList) {
            MyActivityDto.MyActivityDetail myActivityDetail = new MyActivityDto.MyActivityDetail(
                    UUIDGenerator.changeUuidToString(r.getActivityId()),
                    r.getTitle(),
                    r.getHostNickname(),
                    r.getQuota(),
                    r.getParticipants(),
                    r.getActivityImageUrl(),
                    r.getRecruitStartAt(),
                    r.getRecruitEndAt(),
                    r.getStartAt(),
                    getActivityStatus(r.getRecruitEndAt(), r.getStartAt()),
                    r.getAddress(),
                    r.getRole() == CrewRole.HOST ? "" : UUIDGenerator.changeUuidToString(r.getApplicationId()),
                    r.getRole().toString(),
                    activityService.getCrewStatusFromActivityDao(r),
                    messageDaoList.stream().anyMatch(message -> message.getActivityId().equals(r.getActivityId())) ?
                            messageDaoList.stream()
                                    .filter(message -> message.getActivityId().equals(r.getActivityId())).findFirst().get().getMessageBody() : ""
            );
            response.add(myActivityDetail);
        }
        return new MyActivityDto(response, new MyActivityDto.Meta(activityDaoList.getContent().size(), !activityDaoList.hasNext()));
    }

    @Transactional
    public ApplicationSettingResDto setApplicationStatus(ApplicationSettingReqDto request, HttpServletRequest servletRequest) {
        OUser host = tokenUtil.getUserFromHeader(servletRequest);
        CrewStatus newStatus = CrewStatus.getLimitedStatus(request.getStatus());
        if (newStatus == null) {
            throw new IllegalRequestException("요청한 크루원 상태가 올바르지 않습니다. NO_SHOW(노쇼), REJECT(거절) 중 하나여야합니다.");
        }

        boolean result = true;
        boolean checkIsHost = false;
        boolean isParticipationAcceptancePossible = false;
        List<String> rejectTargetNicknames = new ArrayList<>();
        UUID activityId = null;
        MessageSendResDto messageId = null;

        for (String applicationId : request.getApplicationId()) {
            Crews application = activityService.findCrew(applicationId);

            if (application.getCrewStatus().equals(CrewStatus.REJECT))
                throw new IllegalRequestException("이미 거절된 지원서입니다. 더이상 수정할 수 없습니다.");

            if (!checkIsHost) {
                OUser user = activityService.findOwner(application.getActivity());
                if (!host.equals(user))
                    throw new IllegalRequestException("해당 요청에 대한 권한이 없습니다. 호스트만 신청자 관리를 할 수 있습니다.");
                checkIsHost = true;
            }

            if (!isParticipationAcceptancePossible) {
                Activity activity = application.getActivity();
                LocalDate recruitEndAt = activity.getRecruitEndAt();
                LocalDateTime startAt = activity.getStartAt();
                if (!isParticipationAcceptPossible(recruitEndAt, startAt))
                    throw new IllegalRequestException("해당 프로젝트가 모집 종료 또는 활동 종료 상태가 아닙니다");
                isParticipationAcceptancePossible = true;
            }

            application.changeCrewStatus(newStatus);

            if (newStatus.equals(CrewStatus.REJECT)) {
                rejectTargetNicknames.add(application.getUser().getNickname());
                if (activityId == null)
                    activityId = application.getActivity().getUuid();
            } else if (newStatus.equals(CrewStatus.NO_SHOW)) {
                publisher.emit(new ActivityEvent(this, application.getUser(), OceanKeeperEventType.ACTIVITY_NO_SHOW_EVENT));
            }

            result &= (application.getCrewStatus() == newStatus);
        }

        if (newStatus.equals(CrewStatus.REJECT)) {
            assert activityId != null;
            messageId = messageService.sendMessage(
                    new MessageSendReqDto(
                            rejectTargetNicknames,
                            MessageSentType.REJECT,
                            UUIDGenerator.changeUuidToString(activityId),
                            request.getRejectReason()),
                    servletRequest);
        }

        if (newStatus.equals(CrewStatus.REJECT))
            return new ApplicationSettingResDto(result, newStatus, messageId.getMessageId());
        else
            return new ApplicationSettingResDto(result, newStatus);
    }

    private static boolean isParticipationAcceptPossible(LocalDate recruitEndAt, LocalDateTime startAt) {
        return getActivityStatus(recruitEndAt, startAt).equals(ActivityStatus.RECRUITMENT_CLOSE)
                || isAcceptancePossibleInClosedActivity(recruitEndAt, startAt);
    }

    private static boolean isAcceptancePossibleInClosedActivity(LocalDate recruitEndAt, LocalDateTime startAt) {
        return getActivityStatus(recruitEndAt, startAt).equals(ActivityStatus.CLOSED) &&
                LocalDateTime.now().isBefore(startAt.plusDays(14));
    }
}
