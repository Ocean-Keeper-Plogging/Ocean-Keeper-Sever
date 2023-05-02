package com.server.oceankeeper.Global.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.Domain.Auth.LoginUser;


import com.server.oceankeeper.Domain.User.dto.LoginReqDto;
import com.server.oceankeeper.Domain.User.dto.LoginResDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
        super(authenticationManager);

        setFilterProcessesUrl("/login");
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        {
            log.debug("디버그 : attemptAuthentication 호출됨");

             try {
                ObjectMapper om = new ObjectMapper();
                LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);

                log.debug("디버그 : 강제 로그인 토큰 발행 ");

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginReqDto.getProvider()+"_"+loginReqDto.getProviderId(), "-");

                log.debug("디버그 : 강제 로그인 진행");
                Authentication authentication = authenticationManager.authenticate(authenticationToken);

                return authentication;

            }catch(Exception e){
                e.printStackTrace();
                throw new InternalAuthenticationServiceException(e.getMessage());
            }
        }
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        AuthResponse.unAuthentication(response, "로그인이 실패하였습니다.", HttpStatus.UNAUTHORIZED);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtConfig.HEADER, jwtToken);

        LoginResDto loginResDto = new LoginResDto("로그인에 성공하였습니다.");

        AuthResponse.success(response, loginResDto);
    }
}
