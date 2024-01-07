package com.server.oceankeeper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class OceanKeeperApplication {
    public static void main(String[] args) {
        SpringApplication.run(OceanKeeperApplication.class, args);
        log.debug("디버그 : 정상적으로 가동되었습니다.");
    }
}
