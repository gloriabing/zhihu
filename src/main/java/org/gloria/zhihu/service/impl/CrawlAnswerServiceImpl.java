package org.gloria.zhihu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.StringUtils;
import org.gloria.zhihu.constant.Api;
import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.Answer;
import org.gloria.zhihu.model.Comment;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.service.ICrawlAnswerService;
import org.gloria.zhihu.util.JacksonUtil;
import org.gloria.zhihu.util.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Create on 2017/1/6 11:33.
 *
 * @author : gloria.
 */
@Service
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

                Element answerUrlElem = document.getElementsByAttributeValue("itemprop", "url").first();
                if (null != answerUrlElem) {
                    answer.setUrl(answerUrlElem.attr("href").startsWith("https") ? answerUrlElem.attr("href") : Api.HOST + answerUrlElem.attr("href"));       
                }
                answer.setAnswerId(document.getElementsByAttributeValue("itemprop", "answer-id").first().attr("content"));
                
                Element authorElem = document.getElementsByClass("author-link").first();
                if (authorElem != null) {
                    answer.setAuthorUrl(authorElem.attr("href").startsWith("https") ? authorElem.attr("href") : "https://www.zhihu.com" + authorElem.attr("href"));
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
    public List<Comment> parseCommentByAnswer(Crawler crawler) {

        String url = crawler.getUri().toString();
        List<Comment> comments = new ArrayList<>();

        try {
            String body = HttpsUtil.get(url, false);
            Document document = Jsoup.parse(body);
            String answerId = document.getElementsByAttributeValue("itemprop", "answer-id").first().attr("content");
            url = Api.COMMENT.replace("([answerId])", answerId).replace("([page])", "1");
            body = HttpsUtil.get(url, false);
            JsonNode jsonNode = JacksonUtil.toJsonNode(body);
            JsonNode paging = jsonNode.get("paging");
            int totalCount = JacksonUtil.getIntValue(paging, "totalCount");
            int perPage = JacksonUtil.getIntValue(paging, "perPage");

            int totalPage = totalCount / perPage + 1;
            comments.addAll(parseComments(jsonNode.get("data"), false));
            for (int i = 1; i < totalPage; i++) {
                url = Api.COMMENT.replace("([answerId])", answerId).replace("([page])", i + "");
                body = HttpsUtil.get(url, false);
                jsonNode = JacksonUtil.toJsonNode(body);
                comments.addAll(parseComments(jsonNode.get("data"), false));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return comments;
    }

    private List<Comment> parseComments(JsonNode node, boolean isZhuanlan) {
        List<Comment> comments = new ArrayList<>();
        for (JsonNode jsonNode : node) {
            String url = JacksonUtil.getTextValue(jsonNode, "href");
            url = url.startsWith("https") ? url : Api.HOST + url;
            String content = JacksonUtil.getTextValue(jsonNode, "content");
            String createdTime = JacksonUtil.getTextValue(jsonNode, "createdTime");
            String inReplyToCommentId = JacksonUtil.getTextValue(jsonNode, "inReplyToCommentId");
            String authorName = JacksonUtil.getTextValue(jsonNode.get("author"), "name");
            String authorSlug = JacksonUtil.getTextValue(jsonNode.get("author"), "slug");
            Long likesCount = JacksonUtil.getLongValue(jsonNode, "likesCount");
            
            Comment comment = new Comment();
            comment.setUrl(url);
            comment.setContent(content);
            comment.setCreatedTime(createdTime);
            comment.setInReplyToCommentId(inReplyToCommentId);
            comment.setAuthor(authorName);
            comment.setAuthor_slug(authorSlug);
            comment.setLikesCount(likesCount);

            if (jsonNode.get("inReplyToUser") != null && !jsonNode.get("inReplyToUser").isNull()) {
                comment.setInReplyToUser(JacksonUtil.getTextValue(jsonNode.get("inReplyToUser"), "name"));
                comment.setInReplyToUser_slug(JacksonUtil.getTextValue(jsonNode.get("inReplyToUser"), "slug"));
            }

            if (StringUtils.isNotBlank(comment.getInReplyToUser())) {
                if (JacksonUtil.getBoolValue(jsonNode, "featured")) {
                    comment.setTitle(comment.getAuthor() + (isZhuanlan ? " (作者) " : " (提问者) ") + " 回复 " + comment.getInReplyToUser());
                } else {
                    comment.setTitle(comment.getAuthor() + " 回复 " + comment.getInReplyToUser());
                } 
            } else {
                if (JacksonUtil.getBoolValue(jsonNode, "featured")) {
                    comment.setTitle(comment.getAuthor() + (isZhuanlan ? " (作者) " : " (提问者) "));
                } else {
                    comment.setTitle(comment.getAuthor());
                } 
            }
            System.out.println(JacksonUtil.toJson(comment));
            comments.add(comment);
        }
        return comments;
    }

    @Override
    public List<Comment> parseCommentByZhuanlan(Crawler crawler) {
        String url = crawler.getUri().toString();
        String id = RegexUtil.regexMatch("^https://zhuanlan\\.zhihu\\.com/p/([\\d]+)", url);

        List<Comment> comments = new ArrayList<>();

        try {
            String body = null;
            int page = 1;
            JsonNode jsonNode = JacksonUtil.toJsonNode("[]");

            do {
                int offset = (page - 1) * 10;
                url = Api.COMMENT_ZHUANLAN.replace("([id])", id).replace("([offset])", String.valueOf(offset));
                System.out.println("____________________________________________________________________________________");
                System.out.println(url);
                System.out.println("____________________________________________________________________________________");
                body = HttpsUtil.get(url, false);
                if (StringUtils.isNotBlank(body)) {
                    jsonNode = JacksonUtil.toJsonNode(body);
                    comments.addAll(parseComments(jsonNode, true));
                    page++;
                }
            } while (jsonNode.size() != 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return comments;
    }
}
