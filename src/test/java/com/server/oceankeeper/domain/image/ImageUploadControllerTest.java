package com.server.oceankeeper.domain.image;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.user.service.LoginService;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.domain.user.service.UserService;
import com.server.oceankeeper.global.config.AwsS3Config;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.handler.CustomExceptionHandler;
import com.server.oceankeeper.global.jwt.JwtAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(value = ProfileImageUploadController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class)
})
@Import(AwsS3Config.class)
class ProfileImageUploadControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private LoginService loginService;
    @MockBean
    private UserService userService;
    @MockBean
    private BasicAWSCredentials basicAWSCredentials;
    @MockBean
    private AmazonS3 amazonS3;
    @MockBean
    private ImageService imageService;

    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private AccessDeniedHandler jwtAccessDeniedHandler;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @WithMockUser
    void editFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("profile","test.png","image/test.png",
                "test".getBytes(StandardCharsets.UTF_8));
        ResultActions resultActions = mvc.perform(multipart("/image/edit/profile")
                        .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void uploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("profile","test.png","image/test.png",
                "test".getBytes(StandardCharsets.UTF_8));
        ResultActions resultActions = mvc.perform(multipart("/image/profile")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        when(imageService.uploadNewProfile(any(),any())).thenReturn("???");

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }
}