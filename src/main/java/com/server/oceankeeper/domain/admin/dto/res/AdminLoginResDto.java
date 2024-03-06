package com.server.oceankeeper.domain.admin.dto.res;

import com.server.oceankeeper.domain.user.dto.TokenInfo;
import lombok.Data;

@Data
public class AdminLoginResDto {
    private final String nickname;
    private final String id;
    private final TokenInfo token;
}
