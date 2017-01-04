package org.gloria.zhihu.service;

import org.gloria.zhihu.model.Answer;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.Question;

import java.util.List;

/**
 * Create on 2016/12/30 17:47.
 *
 * @author : gloria.
 */
public interface ICrawlQuestionService {

    Question parseQuestion(Crawler crawler);

    List<Answer> parseAnswerByQuestion(Crawler crawler);

    List<Answer> parseTop10Answers(Crawler crawler);
}
