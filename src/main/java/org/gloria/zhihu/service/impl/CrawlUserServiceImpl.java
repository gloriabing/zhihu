package org.gloria.zhihu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.StringUtils;
import org.gloria.zhihu.constant.Api;
import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.*;
import org.gloria.zhihu.service.ICrawlUserService;
import org.gloria.zhihu.util.JacksonUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;

/**
 * Create on 2016/12/23 16:01.
 *
 * @author : gloria.
 */
@Service
public class CrawlUserServiceImpl implements ICrawlUserService {

    private final Logger ERROR = LoggerFactory.getLogger("error");
    private final Logger LOG = LoggerFactory.getLogger("service");

    /**
     * 发现更多用户链接
     *      1. 通过首页为入口提取首页中出现的用户url
     *      2. 以用户页面为入口，提取用户的关注与被关注用户
     * @param crawler
     * @return
     */
    @SuppressWarnings("Duplicates")
    @Override
    public List<Crawler> parseCatalog(Crawler crawler) {

        List<Crawler> crawlers = new ArrayList<>();
        
        try {
            if (crawler.getCrawlType().equals(CrawlType.SEED)) {
                String body = HttpsUtil.get(crawler.getUri().toString(), true);
                Document document = Jsoup.parse(body);
                //首页
                Element feedList = document.getElementById("js-home-feed-list");
                Elements feeds = feedList.getElementsByClass("feed-item-inner");
                for (Element feed : feeds) {
                    try {
                        Element authorElem = feed.getElementsByAttributeValue("class", "zg-link author-link").first();
                        if (authorElem == null) {
                            authorElem = feed.getElementsByAttributeValue("class", "zm-item-link-avatar").first();
                        }
                        String href = authorElem.attr("href");
                        if (!href.startsWith("https")) {
                            href = Api.HOST + href;
                        }
                        Crawler c = new Crawler();
                        c.setUri(URI.create(href));
                        c.setCrawlType(CrawlType.CONTENT);
                        crawlers.add(c);

                        Crawler c2 = new Crawler();
                        c2.setUri(URI.create(href));
                        c2.setCrawlType(CrawlType.CATALOG);
                        crawlers.add(c2);
                    } catch (Exception e) {
                        ERROR.info("", e);
                    }
                }
            } else {
                //用户页面

                String url = Api.FOLLOW_PREFIX + crawler.getUri().toString().substring(Api.PEOPLE_HOST.length()) + Api.FOLLOWEES_SUFFIX;
                String body = HttpsUtil.get(url, false);
                JsonNode json = JacksonUtil.toJsonNode(body);
                JsonNode data = json.get("data");
                while (data.size() != 0) {
                    Iterator it = data.elements();
                    while (it.hasNext()) {
                        JsonNode node = (JsonNode) it.next();
                        String type = node.get("type").asText();
                        String url_token = URLEncoder.encode(node.get("url_token").asText());
                        String userUrl = Api.HOST + "/" + type + "/" + url_token;
                        Crawler c = new Crawler();
                        c.setCrawlType(CrawlType.CONTENT);
                        c.setUri(URI.create(userUrl));
                        crawlers.add(c);

                        Crawler c2 = new Crawler();
                        c2.setCrawlType(CrawlType.CATALOG);
                        c2.setUri(URI.create(userUrl));
                        crawlers.add(c2);
                    }
                    try {
                        body = HttpsUtil.get(json.get("paging").get("next").asText(), false);
                        json = JacksonUtil.toJsonNode(body);
                        data = json.get("data");
                    } catch (Exception e){
                        ERROR.info(json.get("paging").get("next").asText());
                        
                        ERROR.info("", e);
                    }
                    try {
                        Thread.sleep((long) (Math.random()*100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                
            } 
        } catch (IOException e) {
            ERROR.info("", e);
        }
        return crawlers;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public User parseContent(Crawler crawler) {
        User user = new User();
        user.setUrl(crawler.getUri().toString());
        try {
            String body = HttpsUtil.get(crawler.getUri().toString(), false);
            Document document = null;
            try {
                document = Jsoup.parse(body);
            } catch (Exception e) {
                return null;
            }
            Element data = document.getElementById("data");
            body = data.attr("data-state");
            JsonNode jsonNode = JacksonUtil.toJsonNode(body);
            JsonNode users = jsonNode.get("entities").get("users");
            JsonNode userNode = null;
            Iterator<Map.Entry<String, JsonNode>> it = users.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> map = it.next();
                if (!map.getKey().equals("gloria_zhang")) {
                    user.setName(map.getKey());
                    userNode = map.getValue();
                    break;
                }
            }
            user.setHeadline(userNode.get("headline").asText());

            //职业经历
            Iterator employmentsNode = userNode.get("employments").iterator();
            List<Employment> employments = new ArrayList<>();
            try {
                while (employmentsNode.hasNext()) {
                    JsonNode node = (JsonNode) employmentsNode.next();
                    Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
                    Employment employment = new Employment();
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonNode> map = iterator.next();
                        if (map.getKey().equals("company")) {
                            employment.setCompany(map.getValue().get("name").asText());
                        }
                        if (map.getKey().equals("job")) {
                            employment.setJob(map.getValue().get("name").asText());
                        }
                    }
                    employments.add(employment);
                }
            } catch (Exception e) {
                System.out.println("employments error, url = " + crawler.getUri().toString());
            }

            user.setEmployments(employments);
            
            String avatarUrlTemplate = userNode.get("avatarUrlTemplate").asText();
            avatarUrlTemplate = avatarUrlTemplate.replace("{size}", "xll");
            user.setMask(avatarUrlTemplate);

            user.setDescription(userNode.get("description").asText());

            try {
                //行业
                user.setBusiness(userNode.get("business").get("name").asText());
            } catch (Exception e) {
                ERROR.info("business error, url = {}", crawler.getUri().toString());
            }

            List<String> locations = new ArrayList<>();
            try {
                //居住地
                Iterator locationsNode = userNode.get("locations").iterator();
                while (locationsNode.hasNext()) {
                    locations.add(((JsonNode) locationsNode.next()).get("name").asText());
                }
            } catch (Exception e) {
                ERROR.info("locations error, url = {}", crawler.getUri().toString());
            }

            user.setLocations(locations);

            List<Education> educations = new ArrayList<>();
            try {
                //教育经历
                Iterator educationsNode = userNode.get("educations").iterator();
                while (educationsNode.hasNext()) {
                    JsonNode node = (JsonNode) educationsNode.next();
                    Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
                    Education education = new Education();
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonNode> map = iterator.next();
                        if (map.getKey().equals("major")) {
                            education.setMajor(map.getValue().get("name").asText());
                        }
                        if (map.getKey().equals("school")) {
                            education.setSchool(map.getValue().get("name").asText());
                        }
                    }
                    educations.add(education);
                }
            } catch (Exception e) {
                ERROR.info("educations error, url = {}", crawler.getUri().toString());
            }
            user.setEducations(educations);
            user.setName(userNode.get("name").asText());
            try {
                user.setWeibo(userNode.get("sinaWeiboUrl").asText());
            } catch (Exception e) {
                ERROR.info("weibo error, url = {}", crawler.getUri().toString());
            }

            user.setVoteupCount(userNode.get("voteupCount").asLong());
            user.setFollowerCount(userNode.get("followerCount").asLong());
            user.setAnswerCount(userNode.get("answerCount").asLong());
            try {
                user.setGender(userNode.get("gender").asInt() == 1 ? "male" : "female");
            } catch (Exception e) {
                ERROR.info("gender error, url = {}", crawler.getUri().toString());
            }


        } catch (IOException e) {
            ERROR.info("", e);
        }
        
        return user;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public UserInfo parseUserInfo(Crawler crawler) {

        UserInfo user = new UserInfo();
        user.setUrl(crawler.getUri().toString());
        try {
            String body = HttpsUtil.get(crawler.getUri().toString(), false);
            Document document = Jsoup.parse(body);
            Element data = document.getElementById("data");
            body = data.attr("data-state");
            JsonNode jsonNode = JacksonUtil.toJsonNode(body);
            JsonNode users = jsonNode.get("entities").get("users");
            JsonNode userNode = null;
            Iterator<Map.Entry<String, JsonNode>> it = users.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> map = it.next();
                if (!map.getKey().equals("gloria_zhang")) {
                    user.setName(map.getKey());
                    userNode = map.getValue();
                    break;
                }
            }
            user.setHeadline(JacksonUtil.getTextValue(userNode,"headline"));
            user.setId(JacksonUtil.getTextValue(userNode, "id"));
            //职业经历
            Iterator employmentsNode = userNode.get("employments").iterator();
            List<Employment> employments = new ArrayList<>();
            try {
                while (employmentsNode.hasNext()) {
                    JsonNode node = (JsonNode) employmentsNode.next();
                    Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
                    Employment employment = new Employment();
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonNode> map = iterator.next();
                        if (map.getKey().equals("company")) {
                            employment.setCompany(map.getValue().get("name").asText());
                        }
                        if (map.getKey().equals("job")) {
                            employment.setJob(map.getValue().get("name").asText());
                        }
                    }
                    employments.add(employment);
                }
            } catch (Exception e) {
                ERROR.info("employments error, url = {}", crawler.getUri().toString());
            }

            user.setEmployments(employments);

            String avatarUrlTemplate = JacksonUtil.getTextValue(userNode, "avatarUrlTemplate");
            avatarUrlTemplate = avatarUrlTemplate.replace("{size}", "xll");
            user.setMask(avatarUrlTemplate);

            user.setDescription(JacksonUtil.getTextValue(userNode, "description"));

            try {
                //行业
                user.setBusiness(userNode.get("business").get("name").asText());
            } catch (Exception e) {
                ERROR.info("business error, url = {}", crawler.getUri().toString());
            }

            List<String> locations = new ArrayList<>();
            try {
                //居住地
                Iterator locationsNode = userNode.get("locations").iterator();
                while (locationsNode.hasNext()) {
                    locations.add(JacksonUtil.getTextValue((JsonNode) locationsNode.next(), "name"));
                }
            } catch (Exception e) {
                ERROR.info("locations error, url = {}", crawler.getUri().toString());
            }

            user.setLocations(locations);

            List<Education> educations = new ArrayList<>();
            try {
                //教育经历
                Iterator educationsNode = userNode.get("educations").iterator();
                while (educationsNode.hasNext()) {
                    JsonNode node = (JsonNode) educationsNode.next();
                    Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
                    Education education = new Education();
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonNode> map = iterator.next();
                        if (map.getKey().equals("major")) {
                            education.setMajor(map.getValue().get("name").asText());
                        }
                        if (map.getKey().equals("school")) {
                            education.setSchool(map.getValue().get("name").asText());
                        }
                    }
                    educations.add(education);
                }
            } catch (Exception e) {
                ERROR.info("educations error, url = {}", crawler.getUri().toString());
            }
            user.setEducations(educations);
            user.setName(JacksonUtil.getTextValue(userNode, "name"));
            try {
                user.setWeibo(JacksonUtil.getTextValue(userNode, "sinaWeiboUrl"));
            } catch (Exception e) {
                ERROR.info("weibo error, url = {}", crawler.getUri().toString());
            }

            user.setVoteupCount(JacksonUtil.getLongValue(userNode, "voteupCount"));
            user.setFollowingCount(JacksonUtil.getLongValue(userNode, "followingCount"));
            user.setFollowerCount(JacksonUtil.getLongValue(userNode, "followerCount"));
            user.setAnswerCount(JacksonUtil.getLongValue(userNode, "answerCount"));
            user.setQuestionCount(JacksonUtil.getLongValue(userNode, "questionCount"));
            user.setMarkedAnswersCount(JacksonUtil.getLongValue(userNode, "markedAnswersCount"));
            user.setArticlesCount(JacksonUtil.getLongValue(userNode, "articlesCount"));
            user.setThankedCount(JacksonUtil.getLongValue(userNode, "thankedCount"));
            user.setFavoritedCount(JacksonUtil.getLongValue(userNode, "favoritedCount"));
            user.setFollowingTopicCount(JacksonUtil.getLongValue(userNode, "followingTopicCount"));
            user.setHostedLiveCount(JacksonUtil.getLongValue(userNode, "hostedLiveCount"));
            user.setFollowingColumnsCount(JacksonUtil.getLongValue(userNode, "followingColumnsCount"));
            user.setFollowingFavlistsCount(JacksonUtil.getLongValue(userNode, "followingFavlistsCount"));
            
            try {
                user.setGender(JacksonUtil.getIntValue(userNode, "gender") == 1 ? "male" : "female");
            } catch (Exception e) {
                ERROR.info("gender error, url = {}", crawler.getUri().toString());
            }

            //认证
            List<String> badges = new ArrayList<>();
            try {
                //居住地
                Iterator badgesNode = userNode.get("badge").iterator();
                while (badgesNode.hasNext()) {
                    badges.add(JacksonUtil.getTextValue((JsonNode) badgesNode.next(), "description"));
                }
            } catch (Exception e) {
                ERROR.info("badge error, url = {}", crawler.getUri().toString());
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 关注了,关注者
     *
     * @param crawler
     * @return
     */
    @SuppressWarnings("Duplicates")
    @Override
    public List<UserInfo> parseUserFollows(Crawler crawler) {
        List<UserInfo> userInfos = new ArrayList<>();
        try {
            String url = "";
            if (crawler.getCrawlType() == CrawlType.FOLLOWEES) {
                url = Api.FOLLOW_PREFIX + crawler.getUri().toString().substring(Api.PEOPLE_HOST.length()) + Api.FOLLOWEES_SUFFIX;
            } else if (crawler.getCrawlType() == CrawlType.FOLLOWERS) {
                url = Api.FOLLOW_PREFIX + crawler.getUri().toString().substring(Api.PEOPLE_HOST.length()) + Api.FOLLOWERS_SUFFIX;
            }
            if (StringUtils.isBlank(url)) {
                return null;
            }
            String body = HttpsUtil.get(url, false);
            JsonNode json = JacksonUtil.toJsonNode(body);
            JsonNode data = json.get("data");
            while (data.size() != 0) {
                Iterator it = data.elements();
                while (it.hasNext()) {
                    JsonNode node = (JsonNode) it.next();
                    UserInfo user = parseFollowUserBasicInfo(node);
                    if (user != null) {
                        userInfos.add(user);
                    }
                    LOG.info(JacksonUtil.toJson(user));
                    
                }
                try {
                    body = HttpsUtil.get(json.get("paging").get("next").asText(), false);
                    json = JacksonUtil.toJsonNode(body);
                    data = json.get("data");
                } catch (Exception e) {
                    ERROR.info(json.get("paging").get("next").asText());
                    ERROR.info("", e);
                }
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
    
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfos;
    }


    private UserInfo parseFollowUserBasicInfo(JsonNode node) {
        try {
            UserInfo user = new UserInfo();

            user.setId(JacksonUtil.getTextValue(node, "id"));
            String avatarUrlTemplate = JacksonUtil.getTextValue(node, "avatar_url_template");
            avatarUrlTemplate = avatarUrlTemplate.replace("{size}", "xll");
            user.setMask(avatarUrlTemplate);

            user.setName(JacksonUtil.getTextValue(node, "name"));
            user.setHeadline(JacksonUtil.getTextValue(node, "headline"));
            user.setAnswerCount(JacksonUtil.getLongValue(node, "answer_count"));
            user.setFollowerCount(JacksonUtil.getLongValue(node, "follower_count"));

            String type = JacksonUtil.getTextValue(node, "type");
            String url_token = URLEncoder.encode(node.get("url_token").asText());
            String url = Api.HOST + "/" + type + "/" + url_token;
            
            user.setUrl(url);
            user.setArticlesCount(JacksonUtil.getLongValue(node, "articles_count"));
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
