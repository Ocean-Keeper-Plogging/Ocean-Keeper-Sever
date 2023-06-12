package com.server.oceankeeper.domain.crew.service;

import com.server.oceankeeper.domain.activity.dto.request.ApplyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.response.ApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.response.MyActivityDao;
import com.server.oceankeeper.domain.activity.dto.response.MyActivityDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.crew.repository.CrewRepository;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
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

    @Transactional
    public Crews addHost(Activity activity, OUser user) {
        Crews crew = Crews.builder()
                .activity(activity)
                .user(user)
                .applyAt(LocalDateTime.now())
                .uuid(UUIDGenerator.createUuid())
                .activityRole(CrewRole.HOST)
                .crewStatus(CrewStatus.IN_PROGRESS)
                .email(user.getEmail())
                .build();
        log.debug("crew : {}", crew);

        return crewRepository.save(crew);
    }

    @Transactional
    public Crews save(Crews crew) {
        return crewRepository.save(crew);
    }

    @Transactional
    public Crews addCrew(ApplyApplicationReqDto request, Activity activity, OUser applyUser) {
        Crews crew = Crews.builder()
                .uuid(UUIDGenerator.createUuid())
                .user(applyUser)
                .activity(activity)
                .crewStatus(CrewStatus.IN_PROGRESS)
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
                .dayOfBirth(request.getDayOfBirth())
                .build();
        crewRepository.save(crew);
        return crew;
    }

    @Transactional
    public List<MyActivityDto> findCrews(MyActivityParam param) {
        List<MyActivityDao> response = crewRepository.getMyActivities(param);
        log.info("findCrews result :{}",response);

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

    @Transactional
    public OUser findOwner(Activity activity) {
        Crews host = crewRepository.findByActivityAndActivityRole(activity, CrewRole.HOST)
                .orElseThrow(() -> new ResourceNotFoundException("해당 활동에 호스트가 존재하지 않습니다."));
        return host.getUser();
    }

    @Transactional
    public Crews findCrew(OUser user, Activity activity) {
        return crewRepository.findByUserAndActivity(user, activity)
                .orElseThrow(() -> new ResourceNotFoundException("해당 활동에 유저가 존재하지 않습니다."));
    }

    @Transactional
    public boolean existCrew(OUser user, Activity activity) {
        return crewRepository.findByUserAndActivity(user, activity).isPresent();
    }

    @Transactional
    public ApplicationReqDto findApplication(OUser user) {
        Crews applicationInfo = crewRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저의 활동이 존재하지 않습니다."));

        return ApplicationReqDto.builder()
                .dayOfBirth(applicationInfo.getDayOfBirth())
                .email(applicationInfo.getEmail())
                .id1365(applicationInfo.getId1365())
                .name(applicationInfo.getName())
                .phoneNumber(applicationInfo.getPhoneNumber())
                .question(applicationInfo.getQuestion())
                .transportation(applicationInfo.getTransportation())
                .startPoint(applicationInfo.getStartPoint())
                .build();
    }
}
