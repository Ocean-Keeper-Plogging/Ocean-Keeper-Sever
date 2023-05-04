package com.server.oceankeeper.Domain.User;



import com.server.oceankeeper.Domain.User.dto.JoinReqDto;
import com.server.oceankeeper.Domain.User.dto.JoinResDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("")
@RestController
public class UserController {

    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(getClass()); //@slf4j 대신에 사용한다.

    @PostMapping("/auth/signup")
    public ResponseEntity<JoinResDto> join(@RequestBody @Valid JoinReqDto joinReqDto, BindingResult bindingResult) {
        log.debug("디버그 : 회원가입 컨트롤러 호출");
        JoinResDto joinResDto = userService.join(joinReqDto);
        log.debug("디버그 : 회원가입 사용자 등록 완료");
        return new ResponseEntity<JoinResDto>(joinResDto ,HttpStatus.CREATED);
    }




}