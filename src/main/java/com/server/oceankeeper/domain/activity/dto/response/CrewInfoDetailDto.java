package com.server.oceankeeper.domain.activity.dto.response;

import com.server.oceankeeper.domain.activity.dao.CrewInfoDetailDao;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.util.UUIDGenerator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class CrewInfoDetailDto {
    private final ActivityInfo activityInfo;
    private final List<CrewInfoDetailData> crewInfo;

    @Data
    public static class ActivityInfo {
        @ApiModelProperty(
                value = "활동 아이디",
                example = "11ee31531aa0db249f0095c688e46305"
        )
        private final String activityId;
        @ApiModelProperty(
                value = "활동 상태",
                example = "OPEN(모집중), RECRUITMENT_CLOSE(모집종료),CLOSED(활동종료), CANCEL(활동취소)"
        )
        private final ActivityStatus activityStatus;
    }

    @Data
    public static class CrewInfoDetailData {
        @ApiModelProperty(
                value = "순번(신청자 순)",
                example = "1"
        )
        private final int number;
        @ApiModelProperty(
                value = "유저이름(신청시 기재한 이름)",
                example = "김철수"
        )
        private final String username;
        @ApiModelProperty(
                value = "유저닉네임(유저의 닉네임)",
                example = "마이클"
        )
        private final String nickname;
        @ApiModelProperty(
                value = "크루원 상태 승인(IN_PROGRESS)/노쇼(NO_SHOW)/활동종료(CLOSED)/거절(REJECT)",
                example = "IN_PROGRESS"
        )
        private final CrewStatus crewStatus;
        @ApiModelProperty(
                value = "지원서 아이디",
                example = "11ee7cb55b2ee9ca86ec07290eccb3ac"
        )
        private final String applicationId;

        public CrewInfoDetailData(int number, CrewInfoDetailDao dao) {
            this.number = number;
            this.username = dao.getUsername();
            this.nickname = dao.getNickname();
            this.crewStatus = dao.getCrewStatus();
            this.applicationId = UUIDGenerator.changeUuidToString(dao.getApplicationId());
        }
    }
}
