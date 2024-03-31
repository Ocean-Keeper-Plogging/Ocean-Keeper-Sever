package com.server.oceankeeper.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.dummy.DummyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled
class JwtAuthenticationFilterTest extends DummyObject {
    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() throws Exception {
//        OUser user = userRepository.save(newUserWithR("test", "oceankeeper", "1"));
//
//        System.out.println("테스트 : " + user);
    }

    @Test
    public void 로그인성공() throws Exception {
//        LoginReqDto loginReqDto = LoginReqDto.builder().provider("oceankeeper").providerId("1").build();
//        String requestBody = om.writeValueAsString(loginReqDto);
//
//        System.out.println("테스트 : " + loginReqDto.toString());
//
//
//        ///when
//        ResultActions resultActions = mvc.perform(post("/auth/login")
//                .content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON));
//
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtConfig.HEADER);
//
//        //디버깅
//        System.out.println("테스트 : " + responseBody);
//        System.out.println("테스트 : " + jwtToken);
//
//        //then
//        resultActions.andExpect(status().isOk());
//        assertNotNull(jwtToken);
//        assertTrue(jwtToken.startsWith(JwtConfig.TOKEN_PREFIX));
//        resultActions.andExpect(jsonPath("$.data.id").value("1"));

    }

    @Test
    public void 로그인실패() throws Exception {
//        LoginReqDto loginReqDto = LoginReqDto.builder().provider("oceankeeper").providerId("1").build();
//
//        String requestBody = om.writeValueAsString(loginReqDto);
//
//        System.out.println("테스트 : " + loginReqDto.toString());
//
//
//        ///when
//        ResultActions resultActions = mvc.perform(post("/auth/login")
//                .content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON));
//
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtConfig.HEADER);
//
//        //디버깅
//        System.out.println("테스트 : " + responseBody);
//        System.out.println("테스트 : " + jwtToken);
//
//        //then
//        resultActions.andExpect(status().isUnauthorized());
    }

}