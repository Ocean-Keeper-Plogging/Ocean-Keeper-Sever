package com.server.oceankeeper.domain.image;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.JwtTokenPayloadException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final com.server.oceankeeper.util.S3Util S3Util;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void removeProfile(OUser user) {
        String fileUrl = user.getProfile();
        String decodedFileUrl = null;
        decodedFileUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);
        String[] pathParts = decodedFileUrl.split("/");
        String fileName = pathParts[pathParts.length - 2] + "/" + pathParts[pathParts.length - 1];

        S3Util.deleteFromS3(fileName);
    }

    @Transactional
    public void updateProfile(OUser user, String newUrl) {
        user.setProfile(newUrl);
        userRepository.save(user);
    }

    public String uploadNewProfile(MultipartFile profile, String dirName) throws IOException {
        File file = S3Util.multipartUpload(profile).orElseThrow(() -> new ResourceNotFoundException("해당 파일 존재하지 않음"));
        return S3Util.uploadS3(file, dirName);
    }

    public OUser getUserFromProviderInfo(String[] providerInfo){
        if (providerInfo.length != 2)
            throw new JwtTokenPayloadException("토큰에 문제가 있습니다. 일치하는 회원이 없습니다.");
        String provider = providerInfo[0];
        String providerId = providerInfo[1];
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new IdNotFoundException("해당 유저를 찾을 수 없습니다."));
    }
}
