package com.server.oceankeeper.domain.admin.controller;

import com.server.oceankeeper.domain.admin.dto.req.AdminLoginReqDto;
import com.server.oceankeeper.domain.admin.dto.req.AdminLogoutReqDto;
import com.server.oceankeeper.domain.admin.dto.res.AdminLoginResDto;
import com.server.oceankeeper.domain.admin.service.AdminService;
import com.server.oceankeeper.domain.user.dto.*;
import com.server.oceankeeper.domain.user.service.LoginService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @ApiOperation(value = "로그인 요청", notes = "어드민 로그인을 요청합니다.", response = AdminLoginResDto.class)
    @PostMapping(value = "/admin/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 201, message = "로그인 성공. 로그인 유저 정보 및 jwt 반환"),
            @ApiResponse(code = 500, message = "서버 에러")})
    public ResponseEntity<APIResponse<AdminLoginResDto>> login(@RequestBody @Valid AdminLoginReqDto loginReqDto, BindingResult bindingResult) {
        Authentication authentication = getAuthentication(loginReqDto);
        AdminLoginResDto response = adminService.login(loginReqDto,authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse(response));
    }

    private Authentication getAuthentication(AdminLoginReqDto request) {
        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.debug("admin login request :{}, auth : {}, auth name :{}", request, authentication, authentication.getName());
        return authentication;
    }

    @ApiOperation(value = "로그아웃 [권한 필요]", notes = "어드민 로그아웃을 요청합니다. 리프레시 토큰을 무효화합니다."
            , response = String.class)
    @PostMapping(value = "/admin/logout",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 201, message = "로그아웃 성공"),
            @ApiResponse(code = 401, message = "권한 없음"),
            @ApiResponse(code = 500, message = "서버 에러")})
    public ResponseEntity<APIResponse<String>> logout(@RequestBody @Valid AdminLogoutReqDto logoutReqDto, BindingResult bindingResult) {
        adminService.logout(logoutReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse("로그아웃 성공"));
    }
}
