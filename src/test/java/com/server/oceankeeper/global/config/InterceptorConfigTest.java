package com.server.oceankeeper.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

class InterceptorConfigTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    @DisplayName("유저 정보를")
    public void userAccessInterceptorTest(){
//        webTestClient.get().uri("/message/user-id/1231").exchange()
//                .expectHeader()
//                .valueMatches();
    }

}