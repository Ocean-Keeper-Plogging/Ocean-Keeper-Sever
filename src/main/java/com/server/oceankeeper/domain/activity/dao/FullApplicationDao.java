package com.server.oceankeeper.domain.activity.dao;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import lombok.Data;

@Data
public class FullApplicationDao {
    private final OUser host;
    private final String profileUrl;
    private final String nickname;

    private final Integer countHosting;
    private final Integer countActivity;
    private final Integer countNoShow;

    private final String username;
    private final String phoneNumber;
    private final String id1365;
    private final String startPoint;
    private final String transportation;

    private final String question;
}
