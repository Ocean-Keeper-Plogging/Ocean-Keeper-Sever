package com.server.oceankeeper.Domain.User;



import com.server.oceankeeper.Domain.User.dto.JoinReqDto;
import com.server.oceankeeper.Domain.User.dto.JoinResDto;
import com.server.oceankeeper.Global.Exception.DuplicatedResourceException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass()); //@slf4j 대신에 사용한다.
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    @Transactional
    public JoinResDto join(JoinReqDto joinReqDto){

        log.debug("디버그 : "+joinReqDto.toEntity()+" by UserService join");
        inspectDuplicatedDeviceToken(joinReqDto);
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



    private void inspectDuplicatedUser(JoinReqDto joinReqDto){
        if(userRepository.existsByProviderAndProviderId(joinReqDto.getProvider(), joinReqDto.getProviderId())) {
            throw new DuplicatedResourceException("로그인을 시도하는 sns 계정이 이미 가입되어 있습니다.");
        }
    }

    private void inspectDuplicatedNickName(JoinReqDto joinReqDto){
        if(userRepository.existsByNickname(joinReqDto.getNickname())){
            throw new DuplicatedResourceException("동일한 닉네임이 이미 존재합니다.");
        }
    }

    private void inspectDuplicatedDeviceToken(JoinReqDto joinReqDto){
        if(userRepository.existsByDeviceToken(joinReqDto.getDeviceToken())){
            throw new DuplicatedResourceException("이미 회원가입되어있습니다.");
        }
    }




}
