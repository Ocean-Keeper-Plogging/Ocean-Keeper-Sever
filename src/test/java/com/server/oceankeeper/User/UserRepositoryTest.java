package com.server.oceankeeper.User;

import com.server.oceankeeper.DTO.User.UserReqDto;
import com.server.oceankeeper.Dummy.DummyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest extends DummyObject {

    @Autowired
    UserRepository userRepository;


    final static String PROVIDER = "naver";
    final static String PROVIDER_ID = "1234";


    @Test
    public void save() throws Exception{
        //given
        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
        joinReqDto.setProvider("naver");
        joinReqDto.setProviderId("12345");
        joinReqDto.setNickname("test");
        joinReqDto.setEmail("kim@naver.com");
        joinReqDto.setProfile("11");
        joinReqDto.setDeviceToken("1");

        User savedUser =userRepository.save(joinReqDto.toEntity());

        System.out.println(savedUser.toString());

        //when


        //then
        assertEquals(savedUser.getEmail(), joinReqDto.getEmail());
        assertEquals(savedUser.getNickname(), joinReqDto.getNickname());
    }

    @Test
    public void find_by_provider_id() throws Exception{
        //given
            User user = newUserWithR("test", PROVIDER, PROVIDER_ID);
            User userSaved = userRepository.save(user);
        //when
            //naver, 1234로 조회

        Optional<User> userFound = userRepository.findByProviderAndProviderId(PROVIDER, PROVIDER_ID);

        //then
        assertEquals(userFound.get(), userSaved);
    }

    @Test
    public void find_by_nickname() throws Exception{
        //given
        User user = newUserWithR("test", PROVIDER, PROVIDER_ID);
        User userSaved = userRepository.save(user);

        //when
        Optional<User> userFound = userRepository.findByNickname(user.getNickname());

        //then
        assertEquals(userFound.get(), userSaved);
    }

    @Test
    public void find_by_email() throws Exception{
        //given
        User user = newUserWithR("test", PROVIDER, PROVIDER_ID);
        User userSaved = userRepository.save(user);

        //when
        Optional<User> userFound = userRepository.findByEmail(user.getEmail());

        //then
        assertEquals(userFound.get(), userSaved);
    }

    @Test
    public void find_by_id() throws Exception{
        //given
        User user = newUserWithR("test", PROVIDER, PROVIDER_ID);
        User userSaved = userRepository.save(user);

        //when
        Optional<User> userFound = userRepository.findById(user.getId());

        //then
        assertEquals(userFound.get(), userSaved);
    }

}