package com.server.oceankeeper.global.markdown;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class HtmlToMarkDownUtilTest {
    @Test
    public void convertToMarkDown() throws Exception {
        //Given
        String result = HtmlToMarkDownUtil.convertToMarkdown("<p><b>This is me.</b></p><p>test <strong>emphasize </strong></p><p><u>underline</u> test</p>");
        //When
        System.out.println(result);
        //Then
        assertThat(result).isEqualTo("**This is me.**\n\n" +
                "test **emphasize**\n\n"+
                "++underline++ test\n");
    }
}