package com.server.oceankeeper.domain.profile;

import com.server.oceankeeper.domain.profile.dto.ProfileResDto;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.service.LoginService;
import com.server.oceankeeper.domain.user.service.UserService;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final LoginService loginService;

    @ApiOperation(value = "썸네일 수정 [권한 필요]", notes = "기존 프로필 이미지 s3 수정 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PutMapping("/image/profile")
    public ResponseEntity<ProfileResDto> edit(@RequestPart("profile") MultipartFile profile, HttpServletRequest request) throws IOException {
        if (profile.isEmpty()) {
            throw new IllegalRequestException("profile 이미지가 정상적으로 전송되지 않았습니다.");
        }
        String[] providerInfo = loginService.getProviderInfoFromHeader(request);
        OUser user = profileService.getUserFromProviderInfo(providerInfo);

        //기존 파일 s3에서 삭제
        profileService.removeProfile(user);

        String url = profileService.uploadNewProfile(profile, "profile");

        profileService.updateProfile(user, url);

        ProfileResDto profileResDto = ProfileResDto.builder().url(url).build();

        return new ResponseEntity<>(profileResDto, HttpStatus.OK);
    }


    @ApiOperation(value = "썸네일 등록 [권한 필요 없음]", notes = "프로필 이미지를 S3에 저장 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping(("/image/profile"))
    public ResponseEntity<ProfileResDto> upload(@RequestPart("profile") MultipartFile profile) throws IOException {
        //TODO: 파일사이즈 줄이기
        if (profile.isEmpty()) {
            throw new IllegalRequestException("profile 이미지가 정상적으로 전송되지 않았습니다.");
        }

        String url = profileService.uploadNewProfile(profile, "profile");
        ProfileResDto profileResDto = ProfileResDto.builder().url(url).build();

        return ResponseEntity.ok(profileResDto);
    }
}