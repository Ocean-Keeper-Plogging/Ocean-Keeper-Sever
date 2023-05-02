package com.server.oceankeeper.Global.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.server.oceankeeper.Domain.User.dto.LoginResDto;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthResponse {

    private static  Logger log = LoggerFactory.getLogger(AuthResponse.class);


    //로그인 성공 응답
    public static void success(HttpServletResponse response, Object dto){
        try{
            ObjectMapper om = new ObjectMapper();
            LoginResDto responseDto = new LoginResDto("로그인 성공");

            String responseBody = om.writeValueAsString(responseDto);
            response.getWriter().println(responseBody);

            response.setContentType("application/json; charset = utf-8");
            response.setStatus(200);
        }catch(IOException e){
            log.error("에러 : 파싱 에러 "+ e.getMessage());
            LoginResDto responseDto = new LoginResDto("로그인 성공 응답 메시지 생성과정에서 오류가 발생했습니다.");
            response.setContentType("application/json; charset = utf-8");
            response.setStatus(500);
        }
    }

    //로그인 실패 응답
    public static void fail(HttpServletResponse response, String msg, HttpStatus httpStatus){
        try{
            ObjectMapper om = new ObjectMapper();
            LoginResDto responseDto = new LoginResDto("로그인 실패");

            String responseBody = om.writeValueAsString(responseDto);
            response.getWriter().println(responseBody);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());

        }catch(Exception e){
            log.error("에러 : 파싱 에러 "+ e.getMessage());
            LoginResDto responseDto = new LoginResDto("로그인 성공 응답 메시지 생성과정에서 오류가 발생했습니다.");
            response.setContentType("application/json; charset = utf-8");
            response.setStatus(500);
        }
    }

    //인증 미학인 응답
    public static void unAuthentication(HttpServletResponse response, String msg, HttpStatus httpStatus){
        try{
            ObjectMapper om = new ObjectMapper();
            LoginResDto responseDto = new LoginResDto("인증되지 않은 사용자입니다.");

            String responseBody = om.writeValueAsString(responseDto);
            response.getWriter().println(responseBody);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());

        }catch(Exception e){
            log.error("에러 : 파싱 에러 "+ e.getMessage());
            LoginResDto responseDto = new LoginResDto("인증 실패 메시지 생성과정에서 오류가 발생했습니다.");
            response.setContentType("application/json; charset = utf-8");
            response.setStatus(500);
        }
    }
}
