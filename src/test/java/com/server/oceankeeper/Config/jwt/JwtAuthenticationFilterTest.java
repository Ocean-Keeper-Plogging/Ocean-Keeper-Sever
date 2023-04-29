package com.server.oceankeeper.Config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.Domain.User.dto.UserReqDto.*;
import com.server.oceankeeper.Dummy.DummyObject;
import com.server.oceankeeper.Domain.User.User;
import com.server.oceankeeper.Domain.User.UserRepository;
import com.server.oceankeeper.Global.Jwt.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;


    @BeforeEach
    public void setUp() throws Exception{
        User user = userRepository.save(newUserWithR("test", "oceankeeper", "1"));

        System.out.println("테스트 : " +user);

    }

    @Test
    public void 로그인성공() throws Exception {
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setProvider("oceankeeper");
        loginReqDto.setProviderId("1");
        String requestBody = om.writeValueAsString(loginReqDto);

        System.out.println("테스트 : " + loginReqDto.toString());


        ///when

        ResultActions resultActions = mvc.perform(post("/api/login")
                                            .content(requestBody)
                                            .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtConfig.HEADER);

        //디버깅
        System.out.println("테스트 : "+responseBody);
        System.out.println("테스트 : "+jwtToken);

        //then

        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtConfig.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.id").value("1"));

    }

    @Test
    public void 로그인실패() throws Exception{
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setProvider("oceankeeper");
        loginReqDto.setProviderId("2");
        String requestBody = om.writeValueAsString(loginReqDto);

        System.out.println("테스트 : " + loginReqDto.toString());


        ///when

        ResultActions resultActions = mvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtConfig.HEADER);

        //디버깅
        System.out.println("테스트 : "+responseBody);
        System.out.println("테스트 : "+jwtToken);

        //then

        resultActions.andExpect(status().isUnauthorized());
    }

}