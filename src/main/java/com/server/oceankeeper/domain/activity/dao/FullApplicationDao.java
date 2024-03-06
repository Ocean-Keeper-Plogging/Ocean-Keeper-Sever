package com.server.oceankeeper.domain.activity.dao;

import com.server.oceankeeper.domain.activity.entity.Activity;
import lombok.Data;

import java.util.UUID;

@Data
public class FullApplicationDao {
    //private final OUser host;
    private final Long hostId;
    private final String profileUrl;
    private final String nickname;

    private final Integer countHosting;
    private final Integer countActivity;
    private final Integer countNoShow;

    private final String crewName;
    private final String phoneNumber;
    private final String id1365;
    private final String startPoint;
    private final String transportation;
    private final String question;
    private final String email;

    private final String supportedTransportation;
}
