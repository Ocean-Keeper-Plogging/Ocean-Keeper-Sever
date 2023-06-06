package com.server.oceankeeper.domain.user.controller;

import com.server.oceankeeper.domain.user.dto.*;
import com.server.oceankeeper.domain.user.service.LoginService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final LoginService loginService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Value("${jwt.password}")
    private String password;

    @ApiOperation(value = "로그인 요청", notes = "oauth 정보와 device token으로 로그인을 요청합니다.", response = LoginResDto.class)
    @PostMapping(value = "/auth/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 201, message = "로그인 성공. 로그인 유저 정보 및 jwt 반환"),
            @ApiResponse(code = 500, message = "서버 에러")})
    public ResponseEntity<LoginResDto> login(@RequestBody @Valid LoginReqDto loginReqDto, BindingResult bindingResult) {
        Authentication authentication = getAuthentication(loginReqDto);
        LoginResDto response = loginService.login(loginReqDto, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Authentication getAuthentication(LoginReqDto loginReqDto) {
        UsernamePasswordAuthenticationToken authenticationToken = loginReqDto.toAuthentication(password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.debug("login request :{}, auth : {}, auth name :{}", loginReqDto, authentication, authentication.getName());
        return authentication;
    }

    @ApiOperation(value = "로그아웃 [권한 필요]", notes = "oauth 정보와 device token으로 로그아웃을 요청합니다. 리프레시 토큰을 무효화합니다."
            , response = TokenInfo.class)
    @PostMapping(value = "/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 201, message = "로그아웃 성공"),
            @ApiResponse(code = 401, message = "권한 없음"),
            @ApiResponse(code = 500, message = "서버 에러")})
    public ResponseEntity<String> logout(@RequestBody @Valid LogoutReqDto logoutReqDto, BindingResult bindingResult) {
        loginService.logout(logoutReqDto);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @ApiOperation(value = "토큰 재발행", notes = "리프레시 토큰으로 액세스 토큰 재발행을 요청합니다.", response = TokenInfo.class)
    @PostMapping(value = "/auth/reissue",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 201, message = "토큰 재발행 성공"),
            @ApiResponse(code = 401, message = "권한 없음"),
            @ApiResponse(code = 500, message = "서버 에러")})
    public ResponseEntity<?> reissue(@RequestBody @Valid TokenRequestDto tokenRequestDto, BindingResult bindingResult) {
        TokenInfo response = loginService.reissue(tokenRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}