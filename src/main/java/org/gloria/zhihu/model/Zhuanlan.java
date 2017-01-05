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
public class Zhuanlan {

    private String url;
    private String titleImage;
    private String rating;

    private String title;
    private List<Topic> topics;
    private String author;
    private String slug;// author id
    private String publishedTime;
    
    private String content;
    private String summary;
    
    private Long commentsCount;
    private Long likesCount;

}
