package com.server.oceankeeper.Config.jwt;

import com.server.oceankeeper.Domain.Auth.LoginUser;
import com.server.oceankeeper.Domain.User.User;
import com.server.oceankeeper.Domain.User.UserEnum.UserRole;
import com.server.oceankeeper.Global.Jwt.JwtConfig;
import com.server.oceankeeper.Global.Jwt.JwtProcess;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProcessTest {
    @Test
    public void jwt토큰생성테스트() throws Exception{
        User user = User.builder().id(1L).role(UserRole.USER).build();
        LoginUser loginUser = new LoginUser(user);


        String jwtToken = JwtProcess.create(loginUser);

        System.out.println("테스트 : "+jwtToken);

        assertTrue(jwtToken.startsWith(JwtConfig.TOKEN_PREFIX));
    }
}