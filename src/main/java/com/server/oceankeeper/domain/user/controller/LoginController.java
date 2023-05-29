package com.server.oceankeeper.domain.user.controller;

import com.server.oceankeeper.domain.profile.dto.ProfileResDto;
import com.server.oceankeeper.domain.user.dto.LoginReqDto;
import com.server.oceankeeper.domain.user.dto.TokenInfo;
import com.server.oceankeeper.domain.user.dto.TokenRequestDto;
import com.server.oceankeeper.domain.user.service.LoginService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @ApiOperation(value = "로그인 요청", notes = "oauth 정보와 device token으로 로그인을 요청합니다.", response = TokenInfo.class)
    @PostMapping(value = "/auth/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid LoginReqDto loginReqDto, BindingResult bindingResult) {
        TokenInfo response = loginService.login(loginReqDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "로그아웃 [권한 필요]", notes = "oauth 정보와 device token으로 로그아웃을 요청합니다. 리프레시 토큰을 무효화합니다."
            , response = TokenInfo.class)
    @PostMapping(value = "/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(@RequestBody @Valid LoginReqDto loginReqDto, BindingResult bindingResult) {
        loginService.logout(loginReqDto);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @ApiOperation(value = "토큰 재발행", notes = "리프레시 토큰으로 액세스 토큰 재발행을 요청합니다.", response = TokenInfo.class)
    @PostMapping(value = "/auth/reissue",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> reissue(@RequestBody @Valid TokenRequestDto tokenRequestDto, BindingResult bindingResult) {
        TokenInfo response = loginService.reissue(tokenRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
