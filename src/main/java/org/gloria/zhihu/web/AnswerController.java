package org.gloria.zhihu.web;

import org.gloria.zhihu.constant.ResponseData;
import org.gloria.zhihu.constant.Result;
import org.gloria.zhihu.model.*;
import org.gloria.zhihu.service.ICrawlAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

/**
 * Create on 2016/12/28 15:48.
 *
 * @author : gloria.
 */
@RestController
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    private ICrawlAnswerService crawlAnswerService;

    @SuppressWarnings("Duplicates")
    @RequestMapping("/info")
    public ResponseData fetchAnswers(@RequestParam String url) {
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create(url));

        ResponseData data = new ResponseData();

        crawler.setCrawlType(CrawlType.ANSWER);
        List<Answer> answers = crawlAnswerService.parseAnswerByQuestion(crawler);
        if (answers.isEmpty()) {
            data.setResult(Result.ERROR);
        } else {
            data.setResult(Result.SUCCESS);
            data.setObject(answers);
        }

        return data;
    }

    @RequestMapping("/top10")
    public ResponseData fetchTop10Answers(@RequestParam String url) {

        Crawler crawler = new Crawler();
        crawler.setUri(URI.create(url));

        ResponseData data = new ResponseData();

        crawler.setCrawlType(CrawlType.ANSWER);
        List<Answer> answers = crawlAnswerService.parseTop10Answers(crawler);
        if (answers.isEmpty()) {
            data.setResult(Result.ERROR);
        } else {
            data.setResult(Result.SUCCESS);
            data.setObject(answers);
        }

        return data;

    }

    @RequestMapping("/comments/{answer}")
    public ResponseData fetchComments(@PathVariable @NotNull String answerUrl) {
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create(answerUrl));

        ResponseData data = new ResponseData();

        crawler.setCrawlType(CrawlType.COMMENT);
        List<Comment> comments = crawlAnswerService.parseCommentByAnswer(crawler);
        if (comments.isEmpty()) {
            data.setResult(Result.ERROR);
        } else {
            data.setResult(Result.SUCCESS);
            data.setObject(comments);
        }

        return data;
    }

    @RequestMapping("/zhuanlan/comments/{url}")
    public ResponseData fetchZhuanlanComments(@PathVariable @NotNull String url) {
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create(url));

        ResponseData data = new ResponseData();

        crawler.setCrawlType(CrawlType.COMMENT_ZHUANLAN);
        List<Comment> comments = crawlAnswerService.parseCommentByZhuanlan(crawler);
        if (comments.isEmpty()) {
            data.setResult(Result.ERROR);
        } else {
            data.setResult(Result.SUCCESS);
            data.setObject(comments);
        }

        return data;
    }
}
