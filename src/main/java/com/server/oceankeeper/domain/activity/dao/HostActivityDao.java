package com.server.oceankeeper.domain.activity.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class HostActivityDao {
    private final UUID uuid;
    private final String title;
}
