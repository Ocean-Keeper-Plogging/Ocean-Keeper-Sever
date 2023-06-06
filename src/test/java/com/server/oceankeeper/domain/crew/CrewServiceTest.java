package com.server.oceankeeper.domain.crew;

import com.server.oceankeeper.domain.activity.dto.request.ApplyActivityReqDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.repository.CrewRepository;
import com.server.oceankeeper.domain.crew.service.CrewService;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.dummy.DummyObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
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
        Activity mockActivity = newMockActivity(5, ActivityStatus.OPEN);
        OUser mockUser = newMockUser(1L, "kim", "naver", "providerId", UUID.randomUUID());
        Crews mockCrew = Crews.builder()
                .activity(mockActivity)
                .user(mockUser)
                .id(1L)
                .activityRole(CrewRole.CREW)
                .crewStatus(CrewStatus.APPLY)
                .uuid(UUID.randomUUID())
                .build();
        when(crewRepository.save(any())).thenReturn(mockCrew);
        ApplyActivityReqDto request = ApplyActivityReqDto.builder()
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
        Crews result = crewService.addCrew(request, mockActivity, mockUser);

        //then
        assertThat(result).isEqualTo(mockCrew);
        assertThat(result.getCrewStatus()).isEqualTo(CrewStatus.APPLY);
    }

    @Test
    void addHost() {
        Activity mockActivity = newMockActivity(5, ActivityStatus.OPEN);
        OUser mockUser = newMockUser(1L, "kim", "naver", "providerId", UUID.randomUUID());
        Crews mockCrew = Crews.builder()
                .activity(Activity.builder().build())
                .id(1L)
                .activityRole(CrewRole.HOST)
                .crewStatus(CrewStatus.IN_PROGRESS)
                .uuid(UUID.randomUUID())
                .build();
        when(crewRepository.save(any())).thenReturn(mockCrew);

        Crews result = crewService.addHost(mockActivity, mockUser);
        assertThat(result).isEqualTo(mockCrew);
        assertThat(result.getCrewStatus()).isEqualTo(CrewStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("날짜 계산하기")
    public void calculateDDay() {
        LocalDateTime end = LocalDateTime.now().plusDays(10).plusYears(1);

        int days = ReflectionTestUtils.invokeMethod(crewService, "calculateDDay", end);

        System.out.println("day : " + days);

        assertThat(days).isEqualTo(376);
    }
}