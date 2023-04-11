package com.server.oceankeeper.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.DTO.ResponseDto;
import com.server.oceankeeper.DTO.User.UserReqDto;
import com.server.oceankeeper.Dummy.DummyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        dataSetting();
    }

    private void dataSetting()
    {
        userRepository.save(newUserWithR("overlap", "sameProvider","1234"));
    }
    @Test
    public void 회원가입정상() throws Exception{
        //given
        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
        joinReqDto.setProvider("naver");
        joinReqDto.setProviderId("1234");
        joinReqDto.setNickname("test");
        joinReqDto.setEmail("kim@naver.com");
        joinReqDto.setProfile("11");

        //만들어둔 회원가입 요청 dto를 json으로 매핑
        String requestBody = om.writeValueAsString(joinReqDto);


        //when
        //json 바디 데이터를 담아서 api/join 호출
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        //결과값의  body를 담아와서 출력해본다.
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        //then
        resultActions.andExpect(status().isCreated());

    }

//    @Test
//    public void join_fail_동일닉네임() throws Exception{
//        //given
//        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
//        joinReqDto.setProvider("naver");
//        joinReqDto.setProviderId("1234");
//        joinReqDto.setNickname("overlap");
//        joinReqDto.setEmail("kim@naver.com");
//        joinReqDto.setProfile("11");
//
//        //만들어둔 회원가입 요청 dto를 json으로 매핑
//        String requestBody = om.writeValueAsString(joinReqDto);
//
//
//        //when
//        //json 바디 데이터를 담아서 api/join 호출
//        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
//        //결과값의  body를 담아와서 출력해본다.
//
//
//        //then
//        resultActions.andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void join_fail_동일ProviderId() throws Exception{
//        //given
//        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
//        joinReqDto.setProvider("sameProvider");
//        joinReqDto.setProviderId("1234");
//        joinReqDto.setNickname("test");
//        joinReqDto.setEmail("kim@naver.com");
//        joinReqDto.setProfile("11");
//
//        //만들어둔 회원가입 요청 dto를 json으로 매핑
//        String requestBody = om.writeValueAsString(joinReqDto);
//
//
//        //when
//        //json 바디 데이터를 담아서 api/join 호출
//        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
//        //결과값의  body를 담아와서 출력해본다.
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);
//        ResponseDto response = om.readValue(responseBody, ResponseDto.class);
//        System.out.println(response.getData());
//        //then
//        resultActions.andExpect(status().isBadRequest());
//
//        assertThat("{email=유효한 이메일 형식으로 작성해주세요}").isEqualTo(response.getData().toString());
//    }





}