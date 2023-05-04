package com.server.oceankeeper.Domain.Profile;

import com.server.oceankeeper.Global.Exception.IllegalRequestException;
import com.server.oceankeeper.Global.Jwt.JwtConfig;
import com.server.oceankeeper.Global.Jwt.JwtProcess;

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


    //썸네일 수정
    @ApiOperation(value = "썸네일 수정 [권한 필요]", notes = "기존 프로필 이미지 s3 수정 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PutMapping("/user/profile")
    public ResponseEntity<ProfileResDto> edit(@RequestPart("profile") MultipartFile profile, HttpServletRequest request) throws IOException {

        if(profile.isEmpty()){
            throw new IllegalRequestException("profile 이미지가 정상적으로 전송되지 않았습니다.");
        }
        String jwtToken = request.getHeader(JwtConfig.HEADER).replace(JwtConfig.TOKEN_PREFIX, "");
        Long id = JwtProcess.toUserId(jwtToken);
        //기존 파일 s3에서 삭제
        profileService.removeProfile(id);
        //s3에 업로드
        String url = profileService.uploadNewProfile(profile, "profile");

        //db 정보 변경하기
        profileService.updateProfile(id, url);

        ProfileResDto profileResDto = ProfileResDto.builder().url(url).build();

        return new ResponseEntity<>(profileResDto, HttpStatus.OK);
    }

    @ApiOperation(value = "썸네일 등록 [권한 필요 없음]", notes = "프로필 이미지를 S3에 저장 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping(("/user/profile"))
    public ResponseEntity<ProfileResDto> uploadProfile(@RequestPart("profile") MultipartFile profile) throws IOException{

        if(profile.isEmpty()){
            throw new IllegalRequestException("profile 이미지가 정상적으로 전송되지 않았습니다.");
        }

        String url = profileService.uploadNewProfile(profile, "profile");

        ProfileResDto profileResDto = ProfileResDto.builder().url(url).build();

        return ResponseEntity.ok(profileResDto);
    }



}