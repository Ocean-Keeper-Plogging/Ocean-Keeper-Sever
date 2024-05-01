package com.server.oceankeeper.domain.user.service;


import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.domain.user.dto.JoinReqDto;
import com.server.oceankeeper.domain.user.dto.JoinResDto;
import com.server.oceankeeper.domain.user.dto.UserIdAndNicknameReqDto;
import com.server.oceankeeper.domain.user.dto.WithdrawalReqDto;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.domain.user.entity.UserRole;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import com.server.oceankeeper.global.exception.DuplicatedResourceException;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Value("${jwt.password}")
    private String password;

    private final EventPublisher publisher;

    @Transactional
    public JoinResDto join(JoinReqDto joinReqDto) {
        log.debug("디버그 : " + joinReqDto + " by UserService join");
        //inspectDuplicatedDeviceToken(joinReqDto.getDeviceToken());
        inspectDuplicatedUser(joinReqDto);
        inspectDuplicatedNickname(joinReqDto.getNickname());
        OUser user = joinReqDto.toEntity();
        user.initializePassword(passwordEncoder.encode(password)); //TODO: 더 나은 보안 방법이 없을까 고민
        OUser userSaved = userRepository.save(user);
        publisher.emit(new ActivityEvent(this, userSaved, OceanKeeperEventType.USER_JOINED_EVENT));
        return new JoinResDto(userSaved);
    }

    @Transactional
    public boolean inspectDuplicatedNickname(String nickname) {
        log.debug("디버그 : " + nickname + " by UserService inspectDuplicatedNickname");
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicatedResourceException("동일한 닉네임이 이미 존재합니다.");
        }
        return true;
    }

    @Transactional
    public void modifyNickname(UserIdAndNicknameReqDto userIdAndNicknameReqDto, HttpServletRequest request) {
        log.debug("디버그 : " + userIdAndNicknameReqDto + " by UserService modifyNickname");
        UUID uuid = UUIDGenerator.changeUuidFromString(userIdAndNicknameReqDto.getUserId());
        String nickname = userIdAndNicknameReqDto.getNickname();

        OUser user = userRepository.findByUuid(uuid).orElseThrow(() -> new IdNotFoundException("해당 아이디가 존재하지 않습니다."));

        inspectDuplicatedNickname(nickname);

        user.changeNickname(nickname);

        publisher.emit(new ActivityEvent(this, user, OceanKeeperEventType.NICKNAME_CHANGE_EVENT));
    }

    @Transactional
    public void withdrawal(@Valid WithdrawalReqDto withdrawalReqDto) {
        log.debug("디버그 : " + withdrawalReqDto + " by UserService withdrawal");

        String provider = withdrawalReqDto.getProvider();
        String providerId = withdrawalReqDto.getProviderId();

        OUser user = userRepository.findByProviderAndProviderId(provider, providerId).orElseThrow(
                () -> new ResourceNotFoundException("해당 유저가 존재하지 않습니다."));
        user.withdraw();
        publisher.emit(new ActivityEvent(this, user, OceanKeeperEventType.USER_WITHDRAWAL_EVENT));
    }

    private void inspectDuplicatedUser(JoinReqDto joinReqDto) {
        if (userRepository.existsByProviderAndProviderId(joinReqDto.getProvider(), joinReqDto.getProviderId())) {
            throw new DuplicatedResourceException("로그인을 시도하는 sns 계정이 이미 가입되어 있습니다.");
        }
    }

    private void inspectDuplicatedDeviceToken(String deviceToken) {
        if (userRepository.existsByDeviceToken(deviceToken)) {
            throw new DuplicatedResourceException("같은 기기로 이미 회원가입되어있습니다.");
        }
    }

    @Transactional
    public OUser findByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("해당 닉네임을 가진 유저가 존재하지 않습니다."));
    }

    @Transactional
    public OUser findByUUID(String userId) {
        return userRepository.findByUuid(UUIDGenerator.changeUuidFromString(userId))
                .orElseThrow(() -> new IdNotFoundException("해당 아이디가 존재하지 않습니다."));
    }

    @Transactional
    public String findNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("해당 닉네임을 가진 유저가 존재하지 않습니다.")).getDeviceToken();
    }

    @Transactional
    public void setAlarm(Boolean alarm, OUser user) {
        user.setAlarm(alarm);
    }

    @Transactional
    public Slice<OUser> findUsersByNotificationAlarm(boolean alarm, Pageable pageable) {
        return userRepository.findAllByAlarmAndRole(alarm, UserRole.USER, pageable);
    }

    public boolean getAlarm(OUser user) {
        return user.isAlarm();
    }
}
