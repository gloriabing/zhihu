package org.gloria.zhihu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.StringUtils;
import org.gloria.zhihu.constant.Api;
import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.Answer;
import org.gloria.zhihu.model.Comment;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.service.ICrawlAnswerService;
import org.gloria.zhihu.utils.JacksonUtil;
import org.gloria.zhihu.utils.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * Create on 2017/1/6 11:33.
 *
 * @author : gloria.
 */
public class CrawlAnswerServiceImpl implements ICrawlAnswerService {
    @Override
    public List<Answer> parseAnswerByQuestion(Crawler crawler) {
        List<Answer> answers = new ArrayList<>();
        String url = crawler.getUri().toString();
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf('?'));
        }
        String id = RegexUtil.regexMatch("^https://www\\.zhihu\\.com/question/([\\d]+)", url);

        int page = 1;
        int pagesize = 10;

        Map<String, Integer> params = new HashMap<>();
        params.put("url_token", Integer.parseInt(id));
        params.put("pagesize", pagesize);

        Map<String, String> formData = new HashMap<>();
        formData.put("method", "next");

        params.put("offset", (page - 1) * pagesize);

        formData.put("params", JacksonUtil.toJson(params));

        String body = HttpsUtil.post(Api.QUESTION_ANSWER, formData);

        while (StringUtils.isNotBlank(body) && JacksonUtil.toJsonNode(body).get("msg").size() != 0) {
            JsonNode jsonNode = JacksonUtil.toJsonNode(body).get("msg");
            for (JsonNode node : jsonNode) {
                Document document = Jsoup.parse(node.asText());
                Answer answer = new Answer();

                Element authorElem = document.getElementsByClass("author-link").first();
                if (authorElem != null) {
                    answer.setUrl(authorElem.attr("href").startsWith("https") ? authorElem.attr("href") : "https://www.zhihu.com" + authorElem.attr("href"));
                } else {
                    authorElem = document.select("span.author-link-line > span.name").first();
                    if (authorElem == null) {
                        authorElem = document.select("div.zm-item-answer-author-info > span.name").first();
                    }
                }

                answer.setAuthor(authorElem.text().trim());

                answer.setContent(document.getElementsByAttributeValue("class", "zm-editable-content clearfix").html());

                answer.setLikesCount(Long.parseLong(document.getElementsByClass("zm-item-vote-info").first().attr("data-votecount")));

                String commentsCount = document.getElementsByAttributeValue("name", "addcomment").first().html();
                commentsCount = RegexUtil.regexMatch(">([\\d]+) 条评论", commentsCount);
                if (StringUtils.isNotBlank(commentsCount)) {
                    answer.setCommentsCount(Long.parseLong(commentsCount));
                }

                answer.setCreatedTime(RegexUtil.regexMatch("发布于 ([\\s\\S]*?)<", document.html()));
                Element avatarElem = document.getElementsByAttributeValue("class", "zm-list-avatar avatar").first();
                if (avatarElem != null) {
                    answer.setAvatar(avatarElem.attr("src"));
                }

                answers.add(answer);
            }

            page++;
            params.put("offset", (page - 1) * pagesize);
            formData.put("params", JacksonUtil.toJson(params));
            body = HttpsUtil.post(Api.QUESTION_ANSWER, formData);
        }
        return answers;
    }

    @Override
    public List<Answer> parseTop10Answers(Crawler crawler) {

        List<Answer> answers = parseAnswerByQuestion(crawler);

        if (answers.isEmpty()) {
            return answers;
        }

        Collections.sort(answers, (o1, o2) -> (int) (o2.getLikesCount() - o1.getLikesCount()));

        if (answers.size() < 10) {
            return answers;
        }
        answers = answers.subList(0, 9);
        return answers;
    }

    @Override
    public List<Comment> parseCommentByQuestion(Crawler crawler) {
        return null;
    }

    @Override
    public List<Comment> parseCommentByZhuanlan(Crawler crawler) {
        return null;
    }
}
