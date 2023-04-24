package com.server.oceankeeper.User;

import com.server.oceankeeper.DTO.User.UserReqDto.*;
import com.server.oceankeeper.DTO.User.UserResDto.*;
import com.server.oceankeeper.Exception.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass()); //@slf4j 대신에 사용한다.
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void inspectDuplicatedUser(JoinReqDto joinReqDto){
        Optional<User> user = userRepository.findByProviderAndProviderId(joinReqDto.getProvider(),joinReqDto.getProviderId());
        if(user.isPresent()){
            throw new CustomApiException("이미 회원가입이 되어있습니다.");
        }
    }

    public void inspectDuplicatedNickName(JoinReqDto joinReqDto){
        Optional<User> userFoundByNickname= userRepository.findByNickname(joinReqDto.getNickname());
        //닉네임 중복된 경우
        if(userFoundByNickname.isPresent()){
            throw new CustomApiException("동일한 닉네임이 이미 존재합니다.");
        }
    }

    @Transactional
    public JoinResDto join(JoinReqDto joinReqDto){

        log.debug("디버그 : "+joinReqDto.toEntity()+" by UserService join");

        //회원가입을 했었는지 검사
        inspectDuplicatedUser(joinReqDto);
        log.debug("디버그 : 중복가입 검사 통과 by UserService join");
        //닉네임이 중복되었는지 검사
        inspectDuplicatedNickName(joinReqDto);
        log.debug("디버그 : 닉네임 중복 검사 통과 by UserService join");
        User user = joinReqDto.toEntity();
        user.setPassword(passwordEncoder.encode("-"));
        User userSaved = userRepository.save(user);


        return new JoinResDto(userSaved);
    }


}
