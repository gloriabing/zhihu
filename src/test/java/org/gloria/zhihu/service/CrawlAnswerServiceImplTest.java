package org.gloria.zhihu.service;

import org.gloria.zhihu.Application;
import org.gloria.zhihu.model.Answer;
import org.gloria.zhihu.model.Comment;
import org.gloria.zhihu.model.CrawlType;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.util.JacksonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.util.List;

/**
 * Create on 2017/1/11 15:17.
 *
 * @author : gloria.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class CrawlAnswerServiceImplTest {

    @Autowired
    private ICrawlAnswerService crawlAnswerService;
    
    @Test
    public void parseAnswerByQuestion() throws Exception {
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create("https://www.zhihu.com/question/21234453"));
        crawler.setCrawlType(CrawlType.ANSWER);

        List<Answer> answers = crawlAnswerService.parseAnswerByQuestion(crawler);
        for (Answer answer : answers) {
            System.out.println(JacksonUtil.toJson(answer));
        }
    }

    @Test
    public void parseTop10Answers() throws Exception {
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create("https://www.zhihu.com/question/21234453"));
        crawler.setCrawlType(CrawlType.ANSWER);

        List<Answer> answers = crawlAnswerService.parseTop10Answers(crawler);
        for (Answer answer : answers) {
            System.out.println(JacksonUtil.toJson(answer));
        }
    }

    @Test
    public void parseCommentByAnswer() throws Exception {
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create("https://www.zhihu.com/question/22176786"));
        crawler.setCrawlType(CrawlType.COMMENT);

        List<Comment> comments = crawlAnswerService.parseCommentByAnswer(crawler);
        for (Comment comment : comments) {
            System.out.println(JacksonUtil.toJson(comment));
        }
    }

    @Test
    public void parseCommentByZhuanlan() throws Exception {
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create("https://zhuanlan.zhihu.com/p/24778946"));
        crawler.setCrawlType(CrawlType.COMMENT_ZHUANLAN);

        List<Comment> comments = crawlAnswerService.parseCommentByZhuanlan(crawler);
        for (Comment comment : comments) {
            System.out.println(JacksonUtil.toJson(comment));
        }
    }

}