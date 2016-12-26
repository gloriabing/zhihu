package org.gloria.zhihu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Create on 2016/12/23 15:53.
 *
 * @author : gloria.
 */
@Setter
@Getter
@ToString
@Document(collection = "db.user")
public class User {

    @Id
    private ObjectId id;
    private String url;
    private String name;    //名字
    private String gender;  //性别
    private String headline;  //一句话介绍

    private List<String> locations; //居住地
    private String business; //所在行业
    private List<Employment> employments; //职业经历

    private List<Education> educations; //教育经历

    private String description; //个人简介

    private String mask; //头像
    
    private String weibo; //微博

    private Long voteupCount;//赞
    private Long followerCount;//关注者数量
    private Long answerCount;//回答数


}
