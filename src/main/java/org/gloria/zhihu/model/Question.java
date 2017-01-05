package org.gloria.zhihu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Create on 2016/12/30 17:17.
 *
 * @author : gloria.
 */
@Getter
@Setter
@ToString
public class Question {

    private String url;

    private List<String> tags;
    private String title;
    private Long commentCount;
    private Long answerCount;

    private String content;
    private Long followerCount;//关注数

    private Long viewCount;
    private Long relatedFollowerCount;
}
