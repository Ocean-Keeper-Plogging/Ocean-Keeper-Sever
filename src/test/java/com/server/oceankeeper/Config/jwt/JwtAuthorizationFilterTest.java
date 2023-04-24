package com.server.oceankeeper.Config.jwt;

import com.server.oceankeeper.Config.Auth.LoginUser;
import com.server.oceankeeper.User.User;
import com.server.oceankeeper.User.UserEnum.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test") //application.yml 어떤 것을 쓸건지
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.MOCK)
class JwtAuthorizationFilterTest {


    @Autowired
    private MockMvc mvc;
//
//
//    @Test
//    public void 인증성공() throws Exception{
//        //given
//        User user = User.builder().id(1L).role(UserRole.USER).build();
//        LoginUser loginUser = new LoginUser(user);
//        String jwtToken = JwtProcess.create(loginUser);
//        //디버깅용
//        System.out.println("테스트 : " + jwtToken);
//
//        //when
//        ResultActions resultActions = mvc.perform(get("/api/s/hello/test").header(JwtConfig.HEADER, jwtToken));
//
//
//        //then
//        resultActions.andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void jwt토큰없어서인증실패() throws Exception{
//        //when
//        ResultActions resultActions = mvc.perform(get("/api/s/hello/test"));
//        //then
//        resultActions.andExpect(status().isUnauthorized());
    }


}