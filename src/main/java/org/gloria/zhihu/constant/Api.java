package org.gloria.zhihu.constant;

/**
 * Create on 2017/1/6 14:22.
 *
 * @author : gloria.
 */
public class Api {

    public static final String HOST = "https://www.zhihu.com";

    public static final String PEOPLE_HOST = "https://www.zhihu.com/people/";

    public static final String FOLLOW_PREFIX = "https://www.zhihu.com/api/v4/members/";

    public static final String FOLLOWEES_SUFFIX = "/followees?include=data%5B%2A%5D.answer_count%2Carticles_count%2Cfollower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F%28type%3Dbest_answerer%29%5D.topics&limit=10&offset=0";
    public static final String FOLLOWERS_SUFFIX = "/followers?include=data%5B%2A%5D.answer_count%2Carticles_count%2Cfollower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F%28type%3Dbest_answerer%29%5D.topics&limit=10&offset=0";

    public static final String QUESTION_ANSWER = "https://www.zhihu.com/node/QuestionAnswerListV2";

    public static final String ZHUANLAN = "https://zhuanlan.zhihu.com/api/posts/";
    
}
