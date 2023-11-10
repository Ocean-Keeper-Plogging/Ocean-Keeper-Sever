package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dao.FullApplicationDao;
import com.server.oceankeeper.domain.activity.dto.FullApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.response.ApplicationDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.service.CrewService;
import com.server.oceankeeper.domain.statistics.dto.ActivityInfoResDto;
import com.server.oceankeeper.domain.statistics.service.ActivityInfoService;
import com.server.oceankeeper.domain.user.dto.UserInfoDto;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.util.TokenUtil;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityInfoCrewFacadeService {
    private final CrewService crewService;
    private final ActivityInfoService activityInfoService;
    private final TokenUtil tokenUtil;

    @Transactional
    public FullApplicationReqDto getFullApplication(String applicationId, HttpServletRequest request) {
        Crews application = crewService.getApplication(applicationId);
        Activity activity = application.getActivity();
        OUser crew = application.getUser();

        OUser host = crewService.findOwner(activity);
        OUser requester = tokenUtil.getUserFromHeader(request);

        if (host != requester) {
            log.error("current user :{}\n host :{}", requester, host);
            throw new IllegalRequestException("요청한 유저에게는 신청서 읽기 권한이 없습니다.");
        }
        UserInfoDto userInfo = UserInfoDto.builder()
                .nickname(crew.getNickname())
                .profile(crew.getProfile())
                .build();
        ActivityInfoResDto activityInfo = activityInfoService.getUserActivityInfo(crew);

        ApplicationDto applicationDto = ApplicationDto.builder()
                .name(application.getName())
                .id1365(application.getId1365())
                .phoneNumber(application.getPhoneNumber())
                .question(application.getQuestion())
                .transportation(application.getTransportation())
                .startPoint(application.getStartPoint())
                .email(application.getEmail())
                .dayOfBirth(application.getDayOfBirth())
                .build();
        return new FullApplicationReqDto(userInfo, applicationDto, activityInfo);
    }
}
