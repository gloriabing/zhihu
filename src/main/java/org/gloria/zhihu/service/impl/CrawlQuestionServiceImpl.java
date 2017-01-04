package org.gloria.zhihu.service.impl;

import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.Answer;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.Question;
import org.gloria.zhihu.service.ICrawlQuestionService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create on 2017/1/3 14:11.
 *
 * @author : gloria.
 */
@Service
public class CrawlQuestionServiceImpl implements ICrawlQuestionService{


    private final String questionUrl = "^https://www\\.zhihu\\.com/question/.*?";
    private final String zhuanlanUrl = "^https://zhuanlan\\.zhihu\\.com/.*?";
    
    @Override
    public Question parseQuestion(Crawler crawler) {
        try {
            String url = crawler.getUri().toString();
            url = url.substring(0, url.indexOf('?'));
            
            Question question = new Question();
            question.setUrl(url);
            String body = HttpsUtil.get(url, false);
            Document document = Jsoup.parse(body);

            if (url.matches(questionUrl)) {
                Elements tagElems = document.getElementsByClass("zm-item-tag");
                List<String> tags = new ArrayList<>();
                for (Element tagElem : tagElems) {
                    tags.add(tagElem.text().trim());
                }
                question.setTags(tags);

                question.setTitle(document.getElementById("zh-question-title").text().trim());

                question.setContent(document.getElementById("zh-question-detail").getElementsByClass("zm-editable-content").first().html());
                
                question.setAnswerCount(Long.parseLong(document.getElementById("zh-question-answer-num").attr("data-num")));

                question.setFollowerCount(Long.parseLong(document.select("div.zh-question-followers-sidebar > div.zg-gray-normal > a > strong").text().trim()));

                Element commentElem = document.getElementById("zh-question-meta-wrap").getElementsByAttributeValue("name", "addcomment").first();
                if (commentElem != null) {
                    String commentCount = commentElem.html();
                    commentCount = regexMatch("([\\d]+) 条评论", commentCount);
                    question.setCommentCount(Long.parseLong(commentCount));
                }

                question.setViewCount(Long.parseLong(regexMatch("被浏览 [\\s\\S]*?<strong>([\\d]+)</strong> 次，", document.html())));
                question.setRelatedFollowerCount(
                        Long.parseLong(
                                regexMatch("相关话题关注者[\\s\\S]*?<strong>([\\d]+)</strong>[\\s\\S]*?人", document.html())
                        ));
                return question;
            } else if (url.matches(zhuanlanUrl)) {
                
            }

            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Answer> parseAnswerByQuestion(Crawler crawler) {
        return null;
    }

    @Override
    public List<Answer> parseTop10Answers(Crawler crawler) {
        return null;
    }

    private String regexMatch(String regexExp, String text) {
        Pattern pattern = Pattern.compile(regexExp);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
