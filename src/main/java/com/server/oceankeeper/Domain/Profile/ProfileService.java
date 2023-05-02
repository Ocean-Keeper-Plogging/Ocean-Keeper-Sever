package com.server.oceankeeper.Domain.Profile;

import com.server.oceankeeper.Domain.User.User;
import com.server.oceankeeper.Domain.User.UserRepository;
import com.server.oceankeeper.Util.S3Util;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;

    private final com.server.oceankeeper.Util.S3Util S3Util;

    private final Logger log = LoggerFactory.getLogger(getClass()); //@slf4j 대신에 사용한다.


    public void removeProfile(Long id){
        if(userRepository.existsById(id)){
            User user = userRepository.findById(id).get();
            String fileUrl = user.getProfile();
            String decodedFileUrl = null;
            try {
                decodedFileUrl = URLDecoder.decode(fileUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            String[] pathParts = decodedFileUrl.split("/");
            String fileName = pathParts[pathParts.length-2]+"/"+pathParts[pathParts.length - 1];

            S3Util.deleteFromS3(fileName);
        }else{
            throw new IllegalArgumentException("잘못된 id입니다.");
        }

    }


    @Transactional
    public void updateProfile(Long userId, String newUrl) {
        if(userRepository.existsById(userId)){
            User user = userRepository.findById(userId).get();
            user.setProfile(newUrl);
            userRepository.save(user);
        }else{
            throw new IllegalArgumentException("잘못된 id입니다.");
        }
    }

    public String uploadNewProfile(MultipartFile profile, String dirName) throws IOException {

        File file = S3Util.multipartUpload(profile).get();

        String url = S3Util.uploadS3(file, dirName);

        return url;
    }


}
