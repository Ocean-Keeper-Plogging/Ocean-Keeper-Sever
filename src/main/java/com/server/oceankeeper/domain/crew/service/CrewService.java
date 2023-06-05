package com.server.oceankeeper.domain.crew;

import com.server.oceankeeper.domain.activity.dto.request.ApplyActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.response.MyActivityDao;
import com.server.oceankeeper.domain.activity.dto.response.MyActivityDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CrewService {
    private final CrewRepository crewRepository;

    public CrewService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }
//    public void addCrew(Activity activity, OUser user) {
//        Crews crew = Crews.builder()
//                .activity(activity)
//                .user(user)
//                .activityRole(CrewRole.CREW)
//                .crewStatus(CrewStatus.IN_PROGRESS)
//                .email(user.getEmail())
//                .
//                .build();
//
//        crewRepository.save(crew);
//    }

    @Transactional
    public Crews addHost(Activity activity, OUser user) {
        Crews crew = Crews.builder()
                .activity(activity)
                .user(user)
                .uuid(UUIDGenerator.createUuid())
                .activityRole(CrewRole.HOST)
                .crewStatus(CrewStatus.IN_PROGRESS)
                .email(user.getEmail())
                .build();
        log.debug("crew : {}", crew);

        return crewRepository.save(crew);
    }

    @Transactional
    public List<Crews> findByUser(OUser user) {
        return crewRepository.findByUser(user);
    }

    @Transactional
    public Crews addCrew(ApplyActivityReqDto request, Activity activity, OUser applyUser) {
        Crews crew = Crews.builder()
                .uuid(UUIDGenerator.createUuid())
                .user(applyUser)
                .activity(activity)
                .crewStatus(CrewStatus.APPLY)
                .applyAt(LocalDateTime.now())
                .email(request.getEmail())
                .activityRole(CrewRole.CREW)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .id1365(request.getId1365())
                .privacyAgreement(request.isPrivacyAgreement())
                .transportation(request.getTransportation())
                .question(request.getQuestion())
                .startPoint(request.getStartPoint())
                .build();
        crewRepository.save(crew);
        return crew;
    }

    public List<MyActivityDto> findCrews(MyActivityParam param) {
        List<MyActivityDao> response = crewRepository.getMyActivities(param);

        List<MyActivityDto> result = new ArrayList<>();
        for (MyActivityDao dao : response) {
            MyActivityDto myActivity = MyActivityDto.builder()
                    .id(UUIDGenerator.changeUuidToString(dao.getUuid()))
                    .dDay(calculateDDay(dao.getStartAt()))
                    .location(dao.getLocation())
                    .startDay(dao.getStartAt())
                    .title(dao.getTitle())
                    .build();
            result.add(myActivity);
        }
        return result;
    }

    private int calculateDDay(LocalDateTime startAt) {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), startAt.toLocalDate());
    }
}
