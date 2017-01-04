package org.gloria.zhihu.web;

import org.gloria.zhihu.constant.ResponseData;
import org.gloria.zhihu.constant.Result;
import org.gloria.zhihu.model.CrawlType;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.Question;
import org.gloria.zhihu.service.ICrawlQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Create on 2016/12/28 15:47.
 *
 * @author : gloria.
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private ICrawlQuestionService crawlQuestionService;

    @RequestMapping("/info")
    public ResponseData fetchQuestion(@RequestParam String url) {
        Crawler crawler = new Crawler();
        crawler.setCrawlType(CrawlType.QUESTION);
        crawler.setUri(URI.create(url));

        Question question = crawlQuestionService.parseQuestion(crawler);

        ResponseData data = new ResponseData();
        if (null != question) {
            data.setResult(Result.SUCCESS);
            data.setObject(question);
        } else {
            data.setResult(Result.ERROR);
        }
        return data;
    }
    
}
