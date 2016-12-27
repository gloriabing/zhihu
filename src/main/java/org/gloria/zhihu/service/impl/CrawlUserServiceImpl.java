package org.gloria.zhihu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.*;
import org.gloria.zhihu.mongodb.dao.UserDao;
import org.gloria.zhihu.service.ICrawlUserService;
import org.gloria.zhihu.utils.JacksonUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Create on 2016/12/23 16:01.
 *
 * @author : gloria.
 */
@Service
public class CrawlUserServiceImpl implements ICrawlUserService {

    private final Logger ERROR = LoggerFactory.getLogger("error");

    /**
     * 发现更多用户链接
     *      1. 通过首页为入口提取首页中出现的用户url
     *      2. 以用户页面为入口，提取用户的关注与被关注用户
     * @param crawler
     * @return
     */
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
                            href = "https://www.zhihu.com" + href;
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

                String url = "https://www.zhihu.com/api/v4/members/" + crawler.getUri().toString().substring("https://www.zhihu.com/people/".length()) + "/followees?include=data%5B%2A%5D.answer_count%2Carticles_count%2Cfollower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F%28type%3Dbest_answerer%29%5D.topics&limit=10&offset=0";
                String body = HttpsUtil.get(url, false);
                JsonNode json = JacksonUtil.toJsonNode(body);
                JsonNode data = json.get("data");
                while (data.size() != 0) {
                    Iterator it = data.elements();
                    while (it.hasNext()) {
                        JsonNode node = (JsonNode) it.next();
                        String type = node.get("type").asText();
                        String url_token = node.get("url_token").asText();
                        String userUrl = "https://www.zhihu.com/" + type + "/" + url_token;
                        Crawler c = new Crawler();
                        c.setCrawlType(CrawlType.CONTENT);
                        c.setUri(URI.create(userUrl));
                        crawlers.add(c);

                        Crawler c2 = new Crawler();
                        c2.setCrawlType(CrawlType.CATALOG);
                        c2.setUri(URI.create(userUrl));
                        crawlers.add(c2);
                    }
                    body = HttpsUtil.get(json.get("paging").get("next").asText(), false);
                    json = JacksonUtil.toJsonNode(body);
                    data = json.get("data");
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

    @Override
    public User parseContent(Crawler crawler) {
        User user = new User();
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
}
