package org.gloria.zhihu.web;

import org.gloria.zhihu.constant.ResponseData;
import org.gloria.zhihu.constant.Result;
import org.gloria.zhihu.model.CrawlType;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.Question;
import org.gloria.zhihu.model.Zhuanlan;
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

    private final String questionUrl = "^https://www\\.zhihu\\.com/question/.*?";
    private final String zhuanlanUrl = "^https://zhuanlan\\.zhihu\\.com/.*?";

    @RequestMapping("/info")
    public ResponseData fetchQuestion(@RequestParam String url) {
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create(url));

        ResponseData data = new ResponseData();
        if (url.matches(questionUrl)) {

            crawler.setCrawlType(CrawlType.QUESTION);
            Question question = crawlQuestionService.parseQuestion(crawler);

            if (null != question) {
                data.setResult(Result.SUCCESS);
                data.setObject(question);
            } else {
                data.setResult(Result.ERROR);
            }
        } else if (url.matches(zhuanlanUrl)) {
            crawler.setCrawlType(CrawlType.Zhuanlan);
            Zhuanlan zhuanlan = crawlQuestionService.parseZhuanlan(crawler);

            if (null != zhuanlan) {
                data.setResult(Result.SUCCESS);
                data.setObject(zhuanlan);
            } else {
                data.setResult(Result.ERROR);
            }
        } else {
            data.setResult(Result.NOT_FOUND);
        } 

        return data;
    }
    
}
