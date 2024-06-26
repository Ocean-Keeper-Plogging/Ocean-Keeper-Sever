package com.server.oceankeeper.domain.crew;

import com.server.oceankeeper.domain.activity.dto.request.ApplyApplicationReqDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.entity.Crews;
import com.server.oceankeeper.domain.crew.repository.CrewRepository;
import com.server.oceankeeper.domain.crew.service.CrewService;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.dummy.DummyObject;
import com.server.oceankeeper.util.UUIDGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CrewServiceTest extends DummyObject {
    @InjectMocks
    private CrewService crewService;

    @Mock
    private CrewRepository crewRepository;

    @Test
    void addCrew() {
        OUser host = newMockUser(1L, "kim", "naver", "providerId", UUID.randomUUID());
        Activity mockActivity = newMockActivity(host, 5,
                ActivityStatus.OPEN,
                LocationTag.JEJU,
                GarbageCategory.COASTAL,
                10,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120013"));
        Crews mockCrew = Crews.builder()
                .activity(mockActivity)
                .user(host)
                .id(1L)
                .activityRole(CrewRole.CREW)
                .crewStatus(CrewStatus.IN_PROGRESS)
                .uuid(UUID.randomUUID())
                .build();
        when(crewRepository.save(any())).thenReturn(mockCrew);
        ApplyApplicationReqDto request = ApplyApplicationReqDto.builder()
                .email("kim@naver.com")
                .name("김둘리")
                .phoneNumber("01012341234")
                .id1365("kim1365")
                .privacyAgreement(true)
                .transportation("자차")
                .question("몇시에끝나요?")
                .startPoint("서울")
                .build();

        //when
        Crews result = crewService.addCrew(request, mockActivity, host, any());

        //then
        assertThat(result).isEqualTo(mockCrew);
        assertThat(result.getCrewStatus()).isEqualTo(CrewStatus.IN_PROGRESS);
    }

    @Test
    void addHost() {
        OUser host = newMockUser(1L, "kim", "naver", "providerId", UUID.randomUUID());
        Activity mockActivity = newMockActivity(host,5,
                ActivityStatus.OPEN,
                LocationTag.EAST,
                GarbageCategory.COASTAL,
                10,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120012"));
        Crews mockCrew = Crews.builder()
                .activity(Activity.builder().build())
                .id(1L)
                .activityRole(CrewRole.HOST)
                .crewStatus(CrewStatus.IN_PROGRESS)
                .uuid(UUID.randomUUID())
                .build();
        when(crewRepository.save(any())).thenReturn(mockCrew);

        Crews result = crewService.addHost(mockActivity, host);
        assertThat(result).isEqualTo(mockCrew);
        assertThat(result.getCrewStatus()).isEqualTo(CrewStatus.IN_PROGRESS);
    }
}