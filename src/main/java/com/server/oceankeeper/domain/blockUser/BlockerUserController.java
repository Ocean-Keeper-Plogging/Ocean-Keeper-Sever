package com.server.oceankeeper.domain.blockUser;

import com.server.oceankeeper.domain.activity.dto.response.ActivityDetailResDto;
import com.server.oceankeeper.domain.blockUser.service.BlockUserService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class BlockerUserController {
    private final BlockUserService blockUserService;

    @ApiOperation(value = "특정 유저 차단하기[권한 필요]", notes = "특정한 유저를 차단합니다.",
            response = String.class)
    @PostMapping(value = "/block", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<String>> getActivityDetail(
            @ApiParam(name = "host-nickname", value = "host 닉네임", defaultValue = "kim", required = true)
            @RequestParam("host-nickname") String hostNickname, HttpServletRequest request) {
        blockUserService.blockUser(hostNickname,request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createPostResponse("차단 성공"));
    }
}
