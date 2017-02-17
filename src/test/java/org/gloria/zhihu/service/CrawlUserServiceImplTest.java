package org.gloria.zhihu.service;

import okhttp3.*;
import org.gloria.zhihu.Application;
import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.CrawlType;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.User;
import org.gloria.zhihu.mongodb.dao.UserDao;
import org.gloria.zhihu.redis.CustomRedisTemplate;
import org.gloria.zhihu.util.JacksonUtil;
import org.gloria.zhihu.util.ResourcesUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create on 2016/12/23 16:14.
 *
 * @author : gloria.
 */
//@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class, DataSourceAutoConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class CrawlUserServiceImplTest {

    private final Logger LOG = LoggerFactory.getLogger("service");
    private final Logger TASK = LoggerFactory.getLogger("task");

    @Autowired
    private ICrawlUserService crawlUserService;

    @Autowired
    private CustomRedisTemplate customRedisTemplate;
    
    @Autowired
    private UserDao userDao;
    @Test
    public void parseCatalog() throws Exception {
//        login();
        
        Crawler crawler = new Crawler();
        crawler.setCrawlType(CrawlType.SEED);
        crawler.setUri(URI.create("https://www.zhihu.com/"));

        List<Crawler> crawlers = crawlUserService.parseCatalog(crawler);
        for (Crawler c : crawlers) {
            System.out.println(c.getUri().toString());
            customRedisTemplate.lpush("crawl:queue", c);
        }

    }

    /**
     * 数据抓取测试
     * @throws Exception
     */
    @Test
    public void testRun() throws Exception {
//        login();
        
        Crawler preCrawl = customRedisTemplate.rpop("crawl:queue", Crawler.class);
        System.out.println(preCrawl);
        while (preCrawl != null) {
            preCrawl = customRedisTemplate.rpop("crawl:queue", Crawler.class);
            if (preCrawl.getCrawlType() == CrawlType.CATALOG) {
//                boolean r = customRedisTemplate.setIfAbsent(preCrawl.getUri().toString(), "");
//                if (r) {
//                    List<Crawler> crawlers = crawlUserService.parseCatalog(preCrawl);
//                    for (Crawler c : crawlers) {
//                        
//                        TASK.info("new url = {}", c.getUri().toString());
//                        
//                        customRedisTemplate.lpush("crawl:queue", c);
//                    }
//                }
                customRedisTemplate.lpush("crawl:queue:catalog", preCrawl);
            } else if (preCrawl.getCrawlType() == CrawlType.CONTENT) {
                User user = crawlUserService.parseContent(preCrawl);
                if (null != user) {
                    userDao.save(user);
                    LOG.info(JacksonUtil.toJson(user));
                    try {
                        Thread.sleep((long) (Math.random()*200));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void login() throws Exception{
        String url = "https://www.zhihu.com/";
        String body = HttpsUtil.get(url, true);

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

    }

    @Test
    public void testCatalog() throws Exception{
        Crawler crawler = new Crawler();
        crawler.setUri(URI.create("https://www.zhihu.com/people/he-feng"));
        crawler.setCrawlType(CrawlType.CATALOG);

        List<Crawler> crawlers = crawlUserService.parseCatalog(crawler);
        for (Crawler c : crawlers) {
            System.out.println(c.getUri().toString());
            
        }
    }
    @Test
    public void testPost() throws Exception{
       
        
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("params", "{\"offset\":20,\"order_by\":\"created\",\"hash_id\":\"f67561b8d8e9cc0bdd33a5aa46e8ff57\"}");
        formBuilder.add("method", "next");
        Request reuqest = new Request.Builder().url("https://www.zhihu.com/people/he-feng/followees")
//                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")
                .addHeader("X-Xsrftoken", "819c02637352c452209a22bbd84c44f3")
                .addHeader("Cookie", "l_n_c=1; _za=258bde61-d23b-404b-8db8-3f90c07b56c2; udid=\"AFCArGxDmQmPTuxRwQig6WsQHFDM3tmv1ho=|1457684153\"; d_c0=\"ADDADRL-tQmPTosgN2w2_UVS7c-Y-kj0CnE=|1459612129\"; _zap=bee4202b-f779-4712-9596-5a3e17a11b90; a_t=\"2.0AABAllQfAAAXAAAA99RlWAAAQJZUHwAAADDADRL-tQkXAAAAYQJVTT7UZVgAfOM3pOUZVgq_H7vG1ITMZAMNoYeWmz-xyPPfFBAl81Bwn4s3SagIqQ==\"; _xsrf=819c02637352c452209a22bbd84c44f3; q_c1=7afed27604d84c2999383014a5c14fd6|1481943565000|1462270099000; _ga=GA1.2.1911791909.1458204130; o_act=login; l_cap_id=\"NmU3MDE5NmM4YjE2NDhmYzk0NmIzOWU3ZjkwZDVhZmI=|1482590807|4b9cb128b8f97714c8133ab940f1fb8131f83f13\"; cap_id=\"ZDQ5YjI1ODdhNDE0NGFmM2JmZGMzMTE5NzFhYjI5ZmM=|1482590807|b60623f4cb20e2b0709d93be110d8cac7f75cdfa\"; r_cap_id=\"MWI4MTcwOTg5MzVhNGUzMGJiNGM4M2NlMWE2MzVhYTU=|1482590808|ed89e82940cade93a3d7728dba4c54fa15c74533\"; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; n_c=1; __utmt=1; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk4zeGVHV0FDeUR1Z1pibElxTnNRMGswQ1NNMFdMZUtVbVJn|1482677281|6dc955cc59016a9bcf3a142e38ab10fbb4983464; __utma=51854390.1911791909.1458204130.1482590811.1482676925.4; __utmb=51854390.6.10.1482676925; __utmc=51854390; __utmz=51854390.1482676925.4.4.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.110--|2=registration_date=20131017=1^3=entry_date=20131017=1; _za=258bde61-d23b-404b-8db8-3f90c07b56c2; udid=\"AFCArGxDmQmPTuxRwQig6WsQHFDM3tmv1ho=|1457684153\"; d_c0=\"ADDADRL-tQmPTosgN2w2_UVS7c-Y-kj0CnE=|1459612129\"; _zap=bee4202b-f779-4712-9596-5a3e17a11b90; a_t=\"2.0AABAllQfAAAXAAAA99RlWAAAQJZUHwAAADDADRL-tQkXAAAAYQJVTT7UZVgAfOM3pOUZVgq_H7vG1ITMZAMNoYeWmz-xyPPfFBAl81Bwn4s3SagIqQ==\"; _xsrf=819c02637352c452209a22bbd84c44f3; q_c1=7afed27604d84c2999383014a5c14fd6|1481943565000|1462270099000; _ga=GA1.2.1911791909.1458204130; o_act=login; l_cap_id=\"NmU3MDE5NmM4YjE2NDhmYzk0NmIzOWU3ZjkwZDVhZmI=|1482590807|4b9cb128b8f97714c8133ab940f1fb8131f83f13\"; cap_id=\"ZDQ5YjI1ODdhNDE0NGFmM2JmZGMzMTE5NzFhYjI5ZmM=|1482590807|b60623f4cb20e2b0709d93be110d8cac7f75cdfa\"; r_cap_id=\"MWI4MTcwOTg5MzVhNGUzMGJiNGM4M2NlMWE2MzVhYTU=|1482590808|ed89e82940cade93a3d7728dba4c54fa15c74533\"; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; n_c=1; __utmt=1; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk4zeGVHV0FDeUR1Z1pibElxTnNRMGswQ1NNMFdMZUtVbVJn|1482720832|51dce9a708b66af935a0c661eb565ae9804463a9; __utma=51854390.1911791909.1458204130.1482584840.1482590811.3; __utmb=51854390.4.10.1482720645; __utmc=51854390; __utmz=51854390.1482590811.3.3.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.110--|2=registration_date=20131017=1^3=entry_date=20131017=1/; __utmv=51854390.010--|2=registration_date=20131017=1^3=entry_date=20160503=1; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk5iUmVHV0FEYlVxU3d4QjlFTUkwbVN1MUhZNTduQU5HbUZ3|1482590829|95962e0bc2a26cdac2cf5003d4a4bbe1b939255c; n_c=1")
                .post(formBuilder.build()).build();
        
        Response response = new OkHttpClient().newBuilder().followRedirects(true)
                .followSslRedirects(true)
                .build().newCall(reuqest).execute();
        String body = response.body().string();
        System.out.println();
        System.out.println(body);
    }
    
    
    @Test
    public void parseContent() throws Exception {
        Crawler crawler = new Crawler();
        crawler.setCrawlType(CrawlType.CONTENT);
        crawler.setUri(URI.create("https://www.zhihu.com/people/fenng"));
        User user = crawlUserService.parseContent(crawler);
        userDao.save(user);
        
        System.out.println(user);
    }

}