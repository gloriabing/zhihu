package org.gloria.zhihu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.CrawlType;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.User;
import org.gloria.zhihu.service.ICrawlUserService;
import org.gloria.zhihu.utils.JacksonUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Create on 2016/12/23 16:01.
 *
 * @author : gloria.
 */
@Service
public class CrawlUserServiceImpl implements ICrawlUserService {

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
            String body = HttpsUtil.get(crawler.getUri().toString());
            Document document = Jsoup.parse(body);
            if (crawler.getCrawlType().equals(CrawlType.SEED)) {
                //首页
                Element feedList = document.getElementById("js-home-feed-list");
                Elements feeds = feedList.getElementsByClass("feed-item-inner");
                for (Element feed : feeds) {
                    Element authorElem = feed.getElementsByAttributeValue("class", "zg-link author-link").first();
                    String href = authorElem.attr("href");
                    String title = authorElem.attr("title");
                    System.out.println(href + "\t" + title);
                    Crawler c = new Crawler();
                    c.setUri(URI.create(href));
                    c.setCrawlType(CrawlType.CONTENT);
                    crawlers.add(c);
                }
            } else {
                //用户页面
            } 
        } catch (IOException e) {
            e.printStackTrace();
        }

        return crawlers;
    }

    @Override
    public User parseContent(Crawler crawler) {
        return null;
    }
}
