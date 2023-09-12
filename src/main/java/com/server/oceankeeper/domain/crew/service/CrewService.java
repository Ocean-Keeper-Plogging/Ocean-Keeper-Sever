package com.server.oceankeeper.domain.crew.service;

import com.server.oceankeeper.domain.activity.dto.request.ApplyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.response.ApplicationReqDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.repository.CrewRepository;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrewService {
    private final CrewRepository crewRepository;

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
                .activityRole(CrewRole.CREW)
                .dayOfBirth(request.getDayOfBirth())
                .crewStatus(CrewStatus.IN_PROGRESS)
                .email(request.getEmail())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .id1365(request.getId1365())
                .applyAt(LocalDateTime.now())
                .privacyAgreement(request.isPrivacyAgreement())
                .transportation(request.getTransportation())
                .question(request.getQuestion())
                .startPoint(request.getStartPoint())
                .build();
        crewRepository.save(crew);
        return crew;
    }

    @Transactional
    public OUser findOwner(Activity activity) {
        Crews host = crewRepository.findByActivityAndActivityRole(activity, CrewRole.HOST)
                .orElseThrow(() -> new ResourceNotFoundException("해당 활동에 호스트가 존재하지 않습니다."));
        return host.getUser();
    }

    @Transactional
    public Crews findApplication(OUser user, Activity activity) {
        return crewRepository.findByUserAndActivity(user, activity)
                .orElseThrow(() -> new ResourceNotFoundException("해당 활동에 유저가 존재하지 않습니다."));
    }

    @Transactional
    public Crews findApplication(OUser user, String applicationId) {
        return crewRepository.findByUserAndUuid(user, UUIDGenerator.changeUuidFromString(applicationId))
                .orElseThrow(() -> new IdNotFoundException("해당 활동에 유저가 존재하지 않습니다."));
    }


    @Transactional
    public boolean existCrew(OUser user, Activity activity) {
        return crewRepository.findByUserAndActivity(user, activity).isPresent();
    }

    @Transactional
    public ApplicationReqDto findApplication(OUser user) {
        Crews applicationInfo = crewRepository.findCrewsByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저의 활동 지원서가 존재하지 않습니다."));

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

    @Transactional
    public ApplicationReqDto findApplication(String applicationId) {
        Crews applicationInfo = crewRepository.findByUuid(UUIDGenerator.changeUuidFromString(applicationId))
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저의 활동 지원서가 존재하지 않습니다."));

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

    @Transactional
    public void deleteCrew(OUser user, Crews crew) {
        crewRepository.delete(crew);
        EventPublisher.raise(new ActivityEvent(this, user, OceanKeeperEventType.ACTIVITY_PARTICIPATION_CANCEL_EVENT));
    }

    @Transactional
    public void deleteByHost(Crews crew) {
        crewRepository.delete(crew);
        //TODO: fetch join 필요성 고려
        EventPublisher.raise(new ActivityEvent(this, crew.getUser(), OceanKeeperEventType.ACTIVITY_REGISTRATION_CANCEL_EVENT));
    }

    public List<Crews> findCrews(Activity activity) {
        return crewRepository.findByActivity(activity);
    }
}
