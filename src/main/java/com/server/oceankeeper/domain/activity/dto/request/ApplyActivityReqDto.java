package com.server.oceankeeper.domain.activity.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.AssertTrue;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class ApplyActivityReqDto {
    private final String activityId;
    private final String name;
    private final String phoneNumber;
    private final String id1365;
    private final String dayOfBirth;
    private final String email;
    private final String startPoint;
    private final String transportation;
    private final String question;

    @AssertTrue
    private final boolean privacyAgreement;
}
