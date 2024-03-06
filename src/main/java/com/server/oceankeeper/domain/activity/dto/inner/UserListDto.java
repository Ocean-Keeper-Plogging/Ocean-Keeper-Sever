package com.server.oceankeeper.domain.activity.dto.inner;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import lombok.Data;

import java.util.List;

@Data
public class UserListDto {
    private final List<OUser> user;
}
