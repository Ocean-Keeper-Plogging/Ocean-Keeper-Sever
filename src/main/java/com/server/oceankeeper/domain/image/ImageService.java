package com.server.oceankeeper.domain.image;

import com.server.oceankeeper.domain.image.dto.ProfileResDto;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.domain.user.service.LoginService;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.global.exception.JwtTokenPayloadException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final UserRepository userRepository;
    private final com.server.oceankeeper.util.S3Util S3Util;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final LoginService loginService;

    @Transactional
    public ProfileResDto edit(MultipartFile file, HttpServletRequest request, String dirName) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalRequestException("profile 이미지가 정상적으로 전송되지 않았습니다.");
        }
        String[] providerInfo = loginService.getProviderInfoFromHeader(request);
        OUser user = getUserFromProviderInfo(providerInfo);

        //기존 파일 s3에서 삭제
        removeProfile(user);

        String url = uploadNewProfile(file, dirName);

        updateProfile(user, url);

        ProfileResDto profileResDto = ProfileResDto.builder().url(url).build();
        return profileResDto;
    }

    @Transactional
    public ProfileResDto upload(MultipartFile file, String dirName) throws IOException {
        //TODO: 파일사이즈 줄이기
        if (file.isEmpty()) {
            throw new IllegalRequestException("profile 이미지가 정상적으로 전송되지 않았습니다.");
        }

        String url = uploadNewProfile(file, dirName);
        ProfileResDto profileResDto = ProfileResDto.builder().url(url).build();
        return profileResDto;
    }
    private void removeProfile(OUser user) {
        String fileUrl = user.getProfile();
        String decodedFileUrl = null;
        decodedFileUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);
        String[] pathParts = decodedFileUrl.split("/");
        String fileName = pathParts[pathParts.length - 2] + "/" + pathParts[pathParts.length - 1];

        S3Util.deleteFromS3(fileName);
    }

    private void updateProfile(OUser user, String newUrl) {
        user.setProfile(newUrl);
        userRepository.save(user);
    }

    private String uploadNewProfile(MultipartFile profile, String dirName) throws IOException {
        File file = S3Util.multipartUpload(profile).orElseThrow(() -> new ResourceNotFoundException("해당 파일 존재하지 않음"));
        return S3Util.uploadS3(file, dirName);
    }

    private OUser getUserFromProviderInfo(String[] providerInfo){
        if (providerInfo.length != 2)
            throw new JwtTokenPayloadException("토큰에 문제가 있습니다. 일치하는 회원이 없습니다.");
        String provider = providerInfo[0];
        String providerId = providerInfo[1];
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new IdNotFoundException("해당 유저를 찾을 수 없습니다."));
    }
}
