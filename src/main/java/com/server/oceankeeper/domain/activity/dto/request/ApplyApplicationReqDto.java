package com.server.oceankeeper.domain.activity.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class ApplyApplicationReqDto {
    @ApiModelProperty(
            value = "활동 아이디",
            example = "11ee2964f8473afb9cf1650479121d20",
            required = true
    )
    @NotEmpty
    private final String activityId;

    @ApiModelProperty(
            value = "활동 신청할 아이디",
            example = "445acd67977d477bbcacabd5136c4800",
            required = true
    )
    @NotEmpty
    private final String userId;

    @ApiModelProperty(
            value = "활동 신청할 유저 이름",
            example = "김둘리"
    )
    @NotEmpty
    private final String name;

    @ApiModelProperty(
            value = "신청자 휴대폰 번호",
            example = "01012345678"
    )
    @Pattern(regexp = "^01([0|1|6|7|8|9])?(\\d{3,4})?(\\d{4})$", message = "전화번호는 하이픈 없이 01XYYYYZZZZ 꼴이어야합니다")
    @NotEmpty
    private final String phoneNumber;

    @ApiModelProperty(
            value = "1365 아이디",
            example = "kim1365"
    )
    private final String id1365;

    @ApiModelProperty(
            value = "생년월일",
            example = "20010305"
    )
    @Pattern(regexp = "^$|^(19\\d\\d|20\\d{2})(0\\d|1[0-2])(0[1-9]|[1-2]\\d|3[0-1])$", message = "생년월일은 하이픈 없이 YYYYMMDD 꼴이어야 합니다")
    private final String dayOfBirth;

    @ApiModelProperty(
            value = "이메일",
            example = "kim@naver.com"
    )
    @Pattern(regexp = "^[a-zA-Z\\d+-\\_.]+@[a-zA-Z\\d-]+\\.[a-zA-Z\\d-.]+$",message = "이메일 형식에 맞아야합니다.")
    @NotEmpty
    private final String email;

    @ApiModelProperty(
            value = "출발 지역명",
            example = "서울시 강남구"
    )
    private final String startPoint;

    @ApiModelProperty(
            value = "이동수단",
            example = "대중교통"
    )
    @NotEmpty(message = "")
    private final String transportation;

    @ApiModelProperty(
            value = "문의사항",
            example = "안녕하세요"
    )
    private final String question;

    @AssertTrue(message = "개인정보 동의는 필수입니다.")
    @ApiModelProperty(
            value = "개인정보 동의(필수 true)",
            example = "true"
    )
    private final boolean privacyAgreement;
}
