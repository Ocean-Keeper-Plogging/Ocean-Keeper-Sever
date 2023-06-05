package com.server.oceankeeper.domain.image;

import com.server.oceankeeper.domain.image.dto.ProfileResDto;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.service.LoginService;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MyImageUploadController {
    private final ImageService imageService;
    private final LoginService loginService;
    private String dirName;

    protected void setDirectoryName(String dirName) {
        this.dirName = dirName;
    }

    protected ResponseEntity<ProfileResDto> editFile(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalRequestException("profile 이미지가 정상적으로 전송되지 않았습니다.");
        }
        String[] providerInfo = loginService.getProviderInfoFromHeader(request);
        OUser user = imageService.getUserFromProviderInfo(providerInfo);

        //기존 파일 s3에서 삭제
        imageService.removeProfile(user);

        String url = imageService.uploadNewProfile(file, dirName);

        imageService.updateProfile(user, url);

        ProfileResDto profileResDto = ProfileResDto.builder().url(url).build();

        return new ResponseEntity<>(profileResDto, HttpStatus.OK);
    }

    protected ResponseEntity<ProfileResDto> uploadFile(MultipartFile file) throws IOException {
        //TODO: 파일사이즈 줄이기
        if (file.isEmpty()) {
            throw new IllegalRequestException("profile 이미지가 정상적으로 전송되지 않았습니다.");
        }

        String url = imageService.uploadNewProfile(file, dirName);
        ProfileResDto profileResDto = ProfileResDto.builder().url(url).build();

        return ResponseEntity.ok(profileResDto);
    }
}
