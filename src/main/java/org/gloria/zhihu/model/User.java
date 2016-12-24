package org.gloria.zhihu.model;

import java.util.List;

/**
 * Create on 2016/12/23 15:53.
 *
 * @author : gloria.
 */
public class User {

    private String name;    //名字
    private String gender;  //性别
    private String headline;  //一句话介绍

    private List<String> locations; //居住地
    private String business; //所在行业
    private List<Employment> employments; //职业经历

    private List<Education> educations; //教育经历

    private String description; //个人简介

    private String mask; //头像
    
    
}
