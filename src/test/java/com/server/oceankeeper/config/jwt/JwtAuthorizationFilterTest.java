package com.server.oceankeeper.config.jwt;

import com.server.oceankeeper.domain.user.entitiy.LoginUser;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.entitiy.UserRole;
import com.server.oceankeeper.domain.user.dto.TokenInfo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test") //application.yml 어떤 것을 쓸건지
@AutoConfigureMockMvc
@Disabled
class JwtAuthorizationFilterTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username="jb",roles="ROLE_USER")
    public void 인증성공() throws Exception {
//        //given
//        OUser user = OUser.builder().id(1L).role(UserRole.USER).build();
//        LoginUser loginUser = new LoginUser(user);
//        TokenInfo jwtToken = JwtProcess.create(loginUser);
//        //디버깅용
//        System.out.println("테스트 : " + jwtToken);
//
//        //when
//        ResultActions resultActions = mvc.perform(post("/auth").header(JwtConfig.HEADER, jwtToken));
//
//        //then
//        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void jwt토큰없어서인증실패() throws Exception {
        //when
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test"));
        //then
        resultActions.andExpect(status().isUnauthorized());
    }
}