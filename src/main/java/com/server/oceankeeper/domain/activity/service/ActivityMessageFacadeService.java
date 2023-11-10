package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dao.ActivityDao;
import com.server.oceankeeper.domain.activity.dto.response.MyActivityDto;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.message.dto.MessageDao;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.service.MessageService;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageFacadeService {
    private final ActivityService activityService;
    private final MessageService messageService;

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
            else {
                log.error("messageDao size :{}", messageDao.size());
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
                    activityService.getActivityStatus(r),
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
}
