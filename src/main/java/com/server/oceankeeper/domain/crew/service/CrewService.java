package com.server.oceankeeper.domain.crew.service;

import com.server.oceankeeper.domain.activity.dao.FullApplicationDao;
import com.server.oceankeeper.domain.activity.dto.request.ApplyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.response.ApplicationDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.entity.Crews;
import com.server.oceankeeper.domain.crew.repository.CrewRepository;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
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
    private final EventPublisher publisher;

    @Transactional
    public Crews addHost(Activity activity, OUser user) {
        Crews crew = Crews.builder()
                .activity(activity)
                .user(user)
                .host(user)
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
    public Crews addCrew(ApplyApplicationReqDto request, Activity activity, OUser applyUser, OUser host) {
        Crews crew = Crews.builder()
                .uuid(UUIDGenerator.createUuid())
                .user(applyUser)
                .activity(activity)
                .host(host)
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
    public ApplicationDto getNotEmptyApplicationDto(OUser user) {
        Crews applicationInfo = crewRepository.findFirstByUserAndNameIsNotNullOrderByIdDesc(user)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저의 활동 지원서가 존재하지 않습니다."));

        return ApplicationDto.builder()
                .dayOfBirth(applicationInfo.getDayOfBirth() == null ? "" : applicationInfo.getDayOfBirth())
                .email(applicationInfo.getEmail() == null ? "" : applicationInfo.getEmail())
                .id1365(applicationInfo.getId1365() == null ? "" : applicationInfo.getId1365())
                .name(applicationInfo.getName() == null ? "" : applicationInfo.getName())
                .phoneNumber(applicationInfo.getPhoneNumber() == null ? "" : applicationInfo.getPhoneNumber())
                .question(applicationInfo.getQuestion() == null ? "" : applicationInfo.getQuestion())
                .transportation(applicationInfo.getTransportation() == null ? "" : applicationInfo.getTransportation())
                .startPoint(applicationInfo.getStartPoint() == null ? "" : applicationInfo.getStartPoint())
                .build();
    }

    @Transactional
    public ApplicationDto getApplicationDto(String applicationId) {
        Crews applicationInfo = getApplication(applicationId);

        return ApplicationDto.builder()
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
    public Crews getApplication(String applicationId) {
        return crewRepository.findByUuid(UUIDGenerator.changeUuidFromString(applicationId))
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저의 활동 지원서가 존재하지 않습니다."));
    }

    @Transactional
    public Crews findCrews(String applicationId) {
        return crewRepository.findByUuid(UUIDGenerator.changeUuidFromString(applicationId))
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저의 활동 지원서가 존재하지 않습니다."));
    }

    @Transactional
    public List<Crews> findCrews(Activity activity) {
        return crewRepository.findByActivity(activity);
    }

    @Transactional
    public List<Crews> findUserCrewInfo(OUser user) {
        return crewRepository.findByUserAndCrewStatus(user, CrewStatus.IN_PROGRESS);
    }

    @Transactional
    public void deleteCrew(OUser user, Crews crew) {
        log.info("JBJB [deleteCrew] crew:{}", crew);
        crewRepository.delete(crew);
        publisher.emit(new ActivityEvent(this, user, OceanKeeperEventType.ACTIVITY_PARTICIPATION_CANCEL_EVENT));
    }

    @Transactional
    public FullApplicationDao getFullApplication(String applicationId) {
        //TODO:querydsl 안 쓰기
        List<FullApplicationDao> result = crewRepository.getApplicationAndActivityInfoAndCrewInfo(UUIDGenerator.changeUuidFromString(applicationId));
        log.info("JBJB result:{}", result);
        if (result.size() != 1) {
            throw new ResourceNotFoundException("신청서에 해당하는 정보가 없습니다.");
        }
        return result.get(0);
    }
}
