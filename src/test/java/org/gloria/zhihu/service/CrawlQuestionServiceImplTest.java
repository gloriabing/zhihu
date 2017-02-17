package org.gloria.zhihu.service;

import org.gloria.zhihu.Application;
import org.gloria.zhihu.model.CrawlType;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.Question;
import org.gloria.zhihu.util.JacksonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;

/**
 * Create on 2017/1/4 17:41.
 *
 * @author : gloria.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class CrawlQuestionServiceImplTest {

    @Autowired
    private ICrawlQuestionService crawlQuestionService;
    
    @Test
    public void parseQuestion() throws Exception {
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create("https://www.zhihu.com/question/53304261"));
        crawler.setCrawlType(CrawlType.QUESTION);
        
        Question question = crawlQuestionService.parseQuestion(crawler);
        System.out.println(JacksonUtil.toJson(question));
        
    }
    
}