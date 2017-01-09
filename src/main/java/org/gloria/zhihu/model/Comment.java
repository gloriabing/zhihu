package org.gloria.zhihu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Create on 2017/1/6 09:40.
 *
 * @author : gloria.
 */
@Getter
@Setter
@ToString
public class Comment {
    private String title;
    private String content;

    private String author;
    private String author_slug;
    private String inReplyToUser;
    private String inReplyToUser_slug;
    private String createdTime;
    private Long likesCount;
    
    
}
