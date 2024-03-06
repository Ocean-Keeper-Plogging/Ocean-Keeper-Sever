package com.server.oceankeeper.domain.activity.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@Builder
public class ApplicationDto {
    @ApiModelProperty(value = "활동 신청할 유저 이름", example = "김둘리")
    private final String name;

    @ApiModelProperty(value = "신청자 휴대폰 번호", example = "01012345678")
    @Pattern(regexp = "^01([0|1|6|7|8|9])?(\\d{3,4})?(\\d{4})$")
    private final String phoneNumber;

    @ApiModelProperty(value = "1365 아이디", example = "kim1365")
    private final String id1365;

    @ApiModelProperty(value = "생년월일", example = "20010305")
    @Pattern(regexp = "^(19\\d\\d|20\\d{2})(0\\d|1[0-2])(0[1-9]|[1-2]\\d|3[0-1])$")
    private final String dayOfBirth;

    @ApiModelProperty(value = "이메일", example = "kim@naver.com")
    @Pattern(regexp = "^[a-zA-Z\\d+-\\_.]+@[a-zA-Z\\d-]+\\.[a-zA-Z\\d-.]+$")
    private final String email;

    @ApiModelProperty(value = "출발 지역명", example = "서울시 강남구")
    private final String startPoint;

    @ApiModelProperty(value = "이동수단", example = "대중교통")
    private final String transportation;

    @ApiModelProperty(value = "문의사항", example = "안녕하세요")
    private final String question;
}
