package com.server.oceankeeper.domain.user.service;


import com.server.oceankeeper.domain.user.dto.JoinReqDto;
import com.server.oceankeeper.domain.user.dto.JoinResDto;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.dummy.DummyObject;
import com.server.oceankeeper.global.exception.DuplicatedResourceException;
import com.server.oceankeeper.util.UUIDGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
@ActiveProfiles("test")
class UserServiceTest extends DummyObject {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void join_success() throws Exception {
        JoinReqDto joinReqDto = JoinReqDto.builder().
                provider("naver").
                providerId("12345").
                nickname("test").
                email("kim@naver.com").
                deviceToken("1").build();

        //stub1.
        when(userRepository.existsByProviderAndProviderId(any(), any())).thenReturn(false);
        when(userRepository.existsByNickname(any())).thenReturn(false);

        //stub2.
        UUID uuid = UUIDGenerator.createUuid();
        String id = UUIDGenerator.changeUuidToString(uuid);
        OUser test = newMockUser(1L, "test", "naver", "1234", uuid);
        when(userRepository.save(any())).thenReturn(test);

        //when
        JoinResDto joinResDto = userService.join(joinReqDto);

        //then
        assertThat(joinResDto.getId()).isEqualTo(id);
        assertThat(joinResDto.getNickname()).isEqualTo("test");
        System.out.println("UserService 회원가입 단위 테스트 결과 :" + joinResDto.toString());
    }

    @Test
    public void join_fail_닉네임중복() throws Exception {
        //given
        JoinReqDto joinReqDto = JoinReqDto.builder().
                provider("naver").
                providerId("12345").
                nickname("test").
                email("kim@naver.com").
                deviceToken("1").build();

        when(userRepository.existsByNickname(any())).thenReturn(true);

        try {
            //when
            userService.join(joinReqDto);
        } catch (DuplicatedResourceException e) {
            //then
            Assertions.assertEquals(e.getClass(), DuplicatedResourceException.class);
        }
    }

    @Test
    public void join_fail_이미회원가입된경우() throws Exception {
        //given
        JoinReqDto joinReqDto = JoinReqDto.builder().
                provider("naver").
                providerId("12345").
                nickname("test").
                email("kim@naver.com").
                deviceToken("1").build();

        when(userRepository.existsByProviderAndProviderId(any(), any())).thenReturn(true);

        try {
            //when
            JoinResDto joinResDto = userService.join(joinReqDto);
        } catch (DuplicatedResourceException e) {
            //then
            Assertions.assertEquals(e.getClass(), DuplicatedResourceException.class);
        }
    }
}