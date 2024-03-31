package com.server.oceankeeper.domain.activity.dao;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entity.Crews;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ClosedActivityDao {
    final Activity activity;
    final Crews crews;
}
