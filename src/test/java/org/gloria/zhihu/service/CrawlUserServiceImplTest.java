package org.gloria.zhihu.service;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import okio.BufferedSink;
import org.gloria.zhihu.Application;
import org.gloria.zhihu.constant.UrlTemplate;
import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.CrawlType;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.utils.JacksonUtil;
import org.gloria.zhihu.utils.ResourcesUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Create on 2016/12/23 16:14.
 *
 * @author : gloria.
 */
//@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class, DataSourceAutoConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CrawlUserServiceImplTest {

    @Autowired
    private ICrawlUserService crawlUserService;
    
    @Test
    public void parseCatalog() throws Exception {
        String url = "https://www.zhihu.com/";
        String body = HttpsUtil.get(url);

        Pattern pattern = Pattern.compile("<input type=\"hidden\" name=\"_xsrf\" value=\"([\\s\\S]*?)\"/>");

        Matcher matcher = pattern.matcher(body);
        String xsrfValue = "";
        if (matcher.find()) {
            xsrfValue = matcher.group(1);
        }

        url = "https://www.zhihu.com/login/email";

        Properties props = ResourcesUtil.getResourceAsProperties(HttpsUtil.class.getClassLoader(), "zhihu.properties");

        Map<String, String> map = new HashMap<>();
        map.put("_xsrf", xsrfValue);
        map.put("remember_me", "true");
        map.put("email", (String) props.get("username"));
        map.put("password", (String) props.get("password"));


        HttpsUtil.post(url, map);
        
        Crawler crawler = new Crawler();
        crawler.setCrawlType(CrawlType.SEED);
        crawler.setUri(URI.create("https://www.zhihu.com/"));

        List<Crawler> crawlers = crawlUserService.parseCatalog(crawler);
        for (Crawler c : crawlers) {
            System.out.println(c.getUri().toString());
        }

    }
//    
//    @Test
//    public void testPost() throws Exception{
//
//        FormBody.Builder formBuilder = new FormBody.Builder();
//        formBuilder.add("params", "{\"offset\":10,\"start\":0}");
//        formBuilder.add("method", "next");
//        Request reuqest = new Request.Builder().url(UrlTemplate.FEEDLIST)
//                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")
//                .addHeader("X-Xsrftoken", "819c02637352c452209a22bbd84c44f3")
//                .addHeader("Cookie", "_za=258bde61-d23b-404b-8db8-3f90c07b56c2; udid=\"AFCArGxDmQmPTuxRwQig6WsQHFDM3tmv1ho=|1457684153\"; d_c0=\"ADDADRL-tQmPTosgN2w2_UVS7c-Y-kj0CnE=|1459612129\"; _zap=bee4202b-f779-4712-9596-5a3e17a11b90; a_t=\"2.0AABAllQfAAAXAAAA99RlWAAAQJZUHwAAADDADRL-tQkXAAAAYQJVTT7UZVgAfOM3pOUZVgq_H7vG1ITMZAMNoYeWmz-xyPPfFBAl81Bwn4s3SagIqQ==\"; _xsrf=819c02637352c452209a22bbd84c44f3; q_c1=7afed27604d84c2999383014a5c14fd6|1481943565000|1462270099000; _ga=GA1.2.1911791909.1458204130; o_act=login; l_cap_id=\"NmU3MDE5NmM4YjE2NDhmYzk0NmIzOWU3ZjkwZDVhZmI=|1482590807|4b9cb128b8f97714c8133ab940f1fb8131f83f13\"; cap_id=\"ZDQ5YjI1ODdhNDE0NGFmM2JmZGMzMTE5NzFhYjI5ZmM=|1482590807|b60623f4cb20e2b0709d93be110d8cac7f75cdfa\"; r_cap_id=\"MWI4MTcwOTg5MzVhNGUzMGJiNGM4M2NlMWE2MzVhYTU=|1482590808|ed89e82940cade93a3d7728dba4c54fa15c74533\"; __utmt=1; __utma=51854390.1911791909.1458204130.1482584840.1482590811.3; __utmb=51854390.2.10.1482590811; __utmc=51854390; __utmz=51854390.1482590811.3.3.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.010--|2=registration_date=20131017=1^3=entry_date=20160503=1; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk5iUmVHV0FEYlVxU3d4QjlFTUkwbVN1MUhZNTduQU5HbUZ3|1482590829|95962e0bc2a26cdac2cf5003d4a4bbe1b939255c; n_c=1")
//                .post(formBuilder.build()).build();
//        
//        Response response = new OkHttpClient().newBuilder()
//                .build().newCall(reuqest).execute();
//        String body = response.body().string();
//        System.out.println();
//        System.out.println(body);
//    }

    @Test
    public void parseContent() throws Exception {

    }

}