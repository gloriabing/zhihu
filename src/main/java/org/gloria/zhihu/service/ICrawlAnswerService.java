package org.gloria.zhihu.service;

import org.gloria.zhihu.model.Answer;
import org.gloria.zhihu.model.Comment;
import org.gloria.zhihu.model.Crawler;

import java.util.List;

/**
 * Create on 2016/12/30 17:47.
 *
 * @author : gloria.
 */
public interface ICrawlAnswerService {


    List<Answer> parseAnswerByQuestion(Crawler crawler);

    List<Answer> parseTop10Answers(Crawler crawler);

    List<Comment> parseCommentByQuestion(Crawler crawler);

    List<Comment> parseCommentByZhuanlan(Crawler crawler);
    
}
