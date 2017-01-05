package org.gloria.zhihu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.*;
import org.gloria.zhihu.service.ICrawlQuestionService;
import org.gloria.zhihu.utils.JacksonUtil;
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


    
    @Override
    public Question parseQuestion(Crawler crawler) {
        try {
            String url = crawler.getUri().toString();
            url = url.substring(0, url.indexOf('?'));
            
            Question question = new Question();
            question.setUrl(url);

                String body = HttpsUtil.get(url, false);
                Document document = Jsoup.parse(body);
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
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Zhuanlan parseZhuanlan(Crawler crawler) {
        String url = crawler.getUri().toString();

        Zhuanlan zhuanlan = new Zhuanlan();
        try {
            String id = regexMatch("^https://zhuanlan\\.zhihu\\.com/p/([\\d]+)", url);
            url = "https://zhuanlan.zhihu.com/api/posts/" + id;
            String body = HttpsUtil.get(url, false);
            JsonNode jsonNode = JacksonUtil.toJsonNode(body);

            if (!jsonNode.get("rating").asText().equals("none")) {
                zhuanlan.setRating(jsonNode.get("rating").asText());
            }

            zhuanlan.setTitle(jsonNode.get("title").asText());
            zhuanlan.setTitleImage(jsonNode.get("titleImage").asText());

            JsonNode topicsNode = jsonNode.get("topics");
            List<Topic> topics = new ArrayList<>();
            for (JsonNode node : topicsNode) {
                Topic topic = new Topic();
                topic.setUrl(node.get("url").asText());
                topic.setName(node.get("name").asText());
                topics.add(topic);
            }
            if (!topics.isEmpty()) {
                zhuanlan.setTopics(topics);
            }

            zhuanlan.setAuthor(jsonNode.get("author").get("name").asText());
            zhuanlan.setSlug(jsonNode.get("author").get("slug").asText());

            zhuanlan.setContent(jsonNode.get("content").asText());

            zhuanlan.setPublishedTime(jsonNode.get("publishedTime").asText());
            zhuanlan.setSummary(jsonNode.get("summary").asText());
            zhuanlan.setCommentsCount(jsonNode.get("commentsCount").asLong());
            zhuanlan.setLikesCount(jsonNode.get("likesCount").asLong());
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return zhuanlan;
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
