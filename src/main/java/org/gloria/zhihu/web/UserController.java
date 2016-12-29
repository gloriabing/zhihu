package org.gloria.zhihu.web;

import org.gloria.zhihu.constant.ResponseData;
import org.gloria.zhihu.constant.Result;
import org.gloria.zhihu.model.CrawlType;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.UserInfo;
import org.gloria.zhihu.service.ICrawlUserService;
import org.gloria.zhihu.utils.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2016/12/28 15:47.
 *
 * @author : gloria.
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ICrawlUserService crawlUserService;

    @RequestMapping("info")
    public ResponseData fetchUser(@RequestParam String url) {
        ResponseData data = new ResponseData();
        
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create(url));
        crawler.setCrawlType(CrawlType.CONTENT);

        UserInfo user = crawlUserService.parseUserInfo(crawler);
        if (user != null) {
            data.setResult(Result.SUCCESS);
            data.setObject(user);
        } else {
            data.setResult(Result.ERROR);
        } 
        
        return data;
    }

    @RequestMapping("followees/{url_token}")
    public ResponseData fetchFolloweesByUser(@PathVariable @NotNull String url_token) {
        ResponseData data = new ResponseData();

        String url = "https://www.zhihu.com/people/" + url_token;

        Crawler crawler = new Crawler();
        crawler.setCrawlType(CrawlType.FOLLOWEES);
        crawler.setUri(URI.create(url));

        List<UserInfo> userInfos = crawlUserService.parseUserFollows(crawler);

        if (!userInfos.isEmpty()) {
            data.setResult(Result.SUCCESS);
            data.setObject(userInfos);
        } else {
            data.setResult(Result.ERROR);
        } 
        return data;
    }

    @RequestMapping("followers/{url_token}")
    public ResponseData fetchFollowersByUser(@PathVariable @NotNull String url_token) {
        ResponseData data = new ResponseData();

        String url = "https://www.zhihu.com/people/" + url_token;

        Crawler crawler = new Crawler();
        crawler.setCrawlType(CrawlType.FOLLOWEES);
        crawler.setUri(URI.create(url));

        List<UserInfo> userInfos = crawlUserService.parseUserFollows(crawler);


        if (!userInfos.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (UserInfo userInfo : userInfos) {
                list.add(JacksonUtil.toJson(userInfo));
            }
            data.setResult(Result.SUCCESS);
            data.setObject(list);
        } else {
            data.setResult(Result.ERROR);
        }
        return data;
    }
    
}
