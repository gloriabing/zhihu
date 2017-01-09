package org.gloria.zhihu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Create on 2016/12/30 17:52.
 *
 * @author : gloria.
 */
@Setter
@Getter
@ToString
public class Answer {

    private String url;
    private String author;
    private String avatar;
    private String content;
    
    private Long commentsCount;

    private String createdTime;
    private Long likesCount;
    
}
