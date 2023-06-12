package com.server.oceankeeper.domain.image;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.image.dto.ProfileResDto;
import com.server.oceankeeper.domain.user.service.LoginService;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.domain.user.service.UserService;
import com.server.oceankeeper.global.config.AwsS3Config;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.global.handler.CustomExceptionHandler;
import com.server.oceankeeper.global.jwt.JwtAuthenticationEntryPoint;
import com.server.oceankeeper.util.TokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(value = ImageUploadController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class)
})
@Import(AwsS3Config.class)
class ImageUploadControllerTest {
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
    @MockBean
    private TokenUtil tokenUtil;

    @Test
    @WithMockUser
    void editFile_profile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("profile","test.png","image/test.png",
                "test".getBytes(StandardCharsets.UTF_8));
        ResultActions resultActions = mvc.perform(multipart("/image/edit/profile")
                        .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        //when(imageService.edit(any(),any(),any())).thenReturn(new ProfileResDto("path","url"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void uploadFile_profile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("profile","test.png","image/test.png",
                "test".getBytes(StandardCharsets.UTF_8));
        ResultActions resultActions = mvc.perform(multipart("/image/profile")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        when(imageService.upload(any(),any())).thenReturn(new ProfileResDto("url"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void editFile_keeper() throws Exception {
        MockMultipartFile file = new MockMultipartFile("keeper","test.png","image/test.png",
                "test".getBytes(StandardCharsets.UTF_8));
        ResultActions resultActions = mvc.perform(multipart("/image/edit/keeper")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void uploadFile_keeper() throws Exception {
        MockMultipartFile file = new MockMultipartFile("keeper","test.png","image/test.png",
                "test".getBytes(StandardCharsets.UTF_8));
        ResultActions resultActions = mvc.perform(multipart("/image/keeper")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        when(imageService.upload(any(),any())).thenReturn(new ProfileResDto("url"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void editFile_story() throws Exception {
        MockMultipartFile file = new MockMultipartFile("story","test.png","image/test.png",
                "test".getBytes(StandardCharsets.UTF_8));
        ResultActions resultActions = mvc.perform(multipart("/image/edit/story")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void uploadFile_story() throws Exception {
        MockMultipartFile file = new MockMultipartFile("story","test.png","image/test.png",
                "test".getBytes(StandardCharsets.UTF_8));
        ResultActions resultActions = mvc.perform(multipart("/image/story")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        ProfileResDto mockResult = new ProfileResDto("url");
        when(imageService.upload(any(),any())).thenReturn(mockResult);

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void test() throws Exception {
        MockMultipartFile file = new MockMultipartFile("test","test.png","image/test.png",
                "test".getBytes(StandardCharsets.UTF_8));
        ResultActions resultActions = mvc.perform(multipart("/image/test")
                .file(file));
        ProfileResDto mockResult = new ProfileResDto("url");
        //when(imageService.upload(any(),any())).thenReturn(mockResult);
        when(imageService.upload(any(),any())).thenThrow(ResourceNotFoundException.class);

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
//        ProfileResDto result = om.readValue(responseBody,ProfileResDto.class);
//        assertThat(result).isEqualTo(mockResult);
    }
}