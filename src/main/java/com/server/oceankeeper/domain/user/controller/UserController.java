package com.server.oceankeeper.domain.user.controller;

import com.server.oceankeeper.domain.user.dto.TokenInfo;
import com.server.oceankeeper.domain.user.service.UserService;
import com.server.oceankeeper.domain.user.dto.JoinReqDto;
import com.server.oceankeeper.domain.user.dto.JoinResDto;
import com.server.oceankeeper.domain.user.dto.UserIdAndNicknameReqDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("")
@RestController
public class UserController {
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ApiOperation(value = "회원 가입", notes = "앱에서 얻은 정보로 회원가입을 요청합니다.", response = JoinResDto.class)
    @PostMapping(value = "/auth/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JoinResDto> join(@RequestBody @Valid JoinReqDto joinReqDto, BindingResult bindingResult) {
        JoinResDto joinResDto = userService.join(joinReqDto);
        return new ResponseEntity<>(joinResDto, HttpStatus.CREATED);
    }

    @ApiOperation(value = "닉네임 중복확인", notes = "닉네임 중복 확인합니다.")
    @GetMapping("/auth")
    public ResponseEntity<?> checkDuplicateNickname(@RequestParam String nickname) {
        userService.inspectDuplicatedNickname(nickname);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ApiOperation(value = "닉네임 변경 [권한 필요]", notes = "닉네임 중복 확인합니다.")
    @PutMapping(value = "/auth/nickname", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyNickname(@RequestBody @Valid UserIdAndNicknameReqDto userIdAndNicknameReqDto,
                                            HttpServletRequest request,
                                            BindingResult bindingResult) {
        userService.modifyNickname(userIdAndNicknameReqDto, request);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @ApiOperation(value = "회원 탈퇴 [미구현][권한 필요]", notes = "회원을 탈퇴합니다.")
    @DeleteMapping(value = "/auth/withdrawal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdrawal(@RequestBody @Valid JoinReqDto joinReqDto, BindingResult bindingResult) {
        userService.withdrawal();
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}