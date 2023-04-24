package com.server.oceankeeper.Util;

import com.amazonaws.services.s3.transfer.Copy;
import com.server.oceankeeper.Exception.CustomApiException;
import com.server.oceankeeper.User.User;
import com.server.oceankeeper.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final S3Service S3Service;

    public User isExist(Long id){
        Optional<User> user = userRepository.findById(id);
        if(user==null){
            throw new IllegalArgumentException("Invalid user id: " + id);
        }
        return user.get();
    }

    public void removeProfile(Long id){
        User user = isExist(id);
        String fileUrl = user.getProfile();
        String decodedFileUrl = null;
        try {
            decodedFileUrl = URLDecoder.decode(fileUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String[] pathParts = decodedFileUrl.split("/");
        String fileName = pathParts[pathParts.length-2]+"/"+pathParts[pathParts.length - 1];

        S3Service.remove(fileName);
    }


    @Transactional
    public void updateProfile(Long userId, String newUrl) {
        User user = isExist(userId);
        //db url 값 변경하기
        user.setProfile(newUrl);
        userRepository.save(user);
    }


}
