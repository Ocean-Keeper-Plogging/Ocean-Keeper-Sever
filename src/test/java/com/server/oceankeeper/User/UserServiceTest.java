package com.server.oceankeeper.User;

import com.server.oceankeeper.DTO.User.UserReqDto.*;
import com.server.oceankeeper.DTO.User.UserResDto.*;
import com.server.oceankeeper.Dummy.DummyObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {

    @InjectMocks
    private UserService userService;


    @Mock
    private UserRepository userRepository;


    @Test
    public void join_success() throws Exception{
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setProvider("naver");
        joinReqDto.setProviderId("1234");
        joinReqDto.setNickname("test");
        joinReqDto.setEmail("kim@naver.com");
        joinReqDto.setProfile("11");


        //stub1.
        //mock 환경이기 때문에 userRepository를 di하지 못하므로 해당 클래스 내 메서드가 호출되었을 경우 대신 사용하는 코드를 작성함
        //유저 provider id가 중복되지 않았음을 가정하여 null을 꺼내온다.
        when(userRepository.findByProviderAndProviderId(any(), any())).thenReturn(Optional.empty());

        //유저 이름이 중복되지 않았음을 가정하여 null을 꺼내온다.
        when(userRepository.findByNickname(any())).thenReturn(Optional.empty());

        //stub2.
        //중복검사를 통과했기 때문에 repository에 저장되었다는 것 상정하여 저장된 객체를 생성함
        User test = newMockUser(1L, "test", "naver", "1234");


        when(userRepository.save(any())).thenReturn(test);

        //when
        JoinResDto joinResDto = userService.join(joinReqDto);

        //then
        assertThat(joinResDto.getId()).isEqualTo(1L);
        assertThat(joinResDto.getNickname()).isEqualTo("test");
        System.out.println("UserService 회원가입 단위 테스트 결과 :" +joinResDto.toString());
    }

    @Test
    public void join_fail_닉네임중복() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setProvider("naver");
        joinReqDto.setProviderId("1234");
        joinReqDto.setNickname("test");
        joinReqDto.setEmail("kim@naver.com");
        joinReqDto.setProfile("11");

        //stub1.
        //유저 이름이 중복되었을 경우에는 예외가 터진다.
        when(userRepository.findByNickname(any())).thenReturn(Optional.of(new User()));

        try {
            //when
            JoinResDto joinResDto = userService.join(joinReqDto);
        } catch (RuntimeException e) {
            //then
            Assertions.assertEquals("동일한 닉네임이 이미 존재합니다.", e.getMessage());
        }
    }

    @Test
    public void join_fail_이미회원가입된경우() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setProvider("naver");
        joinReqDto.setProviderId("1234");
        joinReqDto.setNickname("test");
        joinReqDto.setEmail("kim@naver.com");
        joinReqDto.setProfile("11");

        //stub1.
        //유저 이름이 중복되었을 경우에는 예외가 터진다.
        when(userRepository.findByProviderAndProviderId(any(), any())).thenReturn(Optional.of(new User()));

        try {
            //when
            JoinResDto joinResDto = userService.join(joinReqDto);
        } catch (RuntimeException e) {
            //then
            Assertions.assertEquals("이미 회원가입이 되어있습니다.", e.getMessage());
        }
    }
}