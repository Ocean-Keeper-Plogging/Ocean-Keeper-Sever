package com.server.oceankeeper.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.server.oceankeeper.Domain.User.dto.JoinReqDto;
import com.server.oceankeeper.Domain.User.dto.JoinResDto;

import com.server.oceankeeper.Dummy.DummyObject;
import com.server.oceankeeper.Domain.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp(){

        userRepository.save(newUserWithR("overlap", "sameProvider","overlap"));


    }


    @Test
    public void 회원가입정상() throws Exception{
        //given

        JoinReqDto joinReqDto = JoinReqDto.builder().
                provider("naver").
                providerId("12345").
                nickname("test").
                email("kim@naver.com").
                deviceToken("1").build();

        //만들어둔 회원가입 요청 dto를 json으로 매핑
        String requestBody = om.writeValueAsString(joinReqDto);

        System.out.println("test : " + joinReqDto.toEntity());

        //when
        //json 바디 데이터를 담아서 api/join 호출
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        //결과값의  body를 담아와서 출력해본다.
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        //then
        resultActions.andExpect(status().isCreated());

    }

    @Test
    public void join_fail_동일닉네임() throws Exception{
        //given

        JoinReqDto joinReqDto = JoinReqDto.builder().
                provider("naver").
                providerId("12345").
                nickname("overlap").
                email("kim@naver.com").
                deviceToken("1").build();

        //만들어둔 회원가입 요청 dto를 json으로 매핑
        String requestBody = om.writeValueAsString(joinReqDto);


        //when
        //json 바디 데이터를 담아서 api/join 호출
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        //결과값의  body를 담아와서 출력해본다.


        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void join_fail_동일ProviderId() throws Exception{

        JoinReqDto joinReqDto = JoinReqDto.builder().
                provider("naver").
                providerId("overlap").
                nickname("1234").
                email("kim@naver.com").
                deviceToken("1").build();

        //만들어둔 회원가입 요청 dto를 json으로 매핑
        String requestBody = om.writeValueAsString(joinReqDto);


        //when
        //json 바디 데이터를 담아서 api/join 호출
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);
        JoinResDto response = om.readValue(responseBody, JoinResDto.class);
        System.out.println(response);
        //then
        resultActions.andExpect(status().isBadRequest());

        assertThat(status().isBadRequest());
    }





}