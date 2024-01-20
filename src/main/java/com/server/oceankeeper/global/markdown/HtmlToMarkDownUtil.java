package com.server.oceankeeper.global.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import static com.vladsch.flexmark.html.HtmlRenderer.HARD_BREAK;
import static com.vladsch.flexmark.html.HtmlRenderer.SOFT_BREAK;

@Component
public class HtmlToMarkDownUtil {
    public static String convertToMarkdown(String html) {

        MutableDataSet options = new MutableDataSet();
        options.set(HARD_BREAK, "false");

        if (isValidHtml(html)) {
            return FlexmarkHtmlConverter.builder(options).build().convert(html);
        } else {
            throw new RuntimeException("It's not html string");
        }
    }

    public static boolean isValidHtml(String html) {
        try {
            Jsoup.parse(html, "", org.jsoup.parser.Parser.htmlParser());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
