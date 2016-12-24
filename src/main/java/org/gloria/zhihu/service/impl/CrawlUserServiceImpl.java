package org.gloria.zhihu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.gloria.zhihu.http.HttpsUtil;
import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.User;
import org.gloria.zhihu.service.ICrawlUserService;
import org.gloria.zhihu.utils.JacksonUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create on 2016/12/23 16:01.
 *
 * @author : gloria.
 */
@Service
public class CrawlUserServiceImpl implements ICrawlUserService {
    
    @Override
    public List<Crawler> parseCatalog(Crawler crawler) {
        int start = crawler.getPage() == 0 ? 0 : crawler.getPage() * 10 - 1;

        String body = HttpsUtil.post(crawler.getUri().toString(), new HashMap<String, String>() {{
            put("params", "{\"offset\":10,\"start\":\"" + start + "\"}");
            put("method", "next");
        }});

        JsonNode jsonNode = JacksonUtil.toJsonNode(body);

        jsonNode.get("msg").elements();
        return null;
    }

    @Override
    public User parseContent(Crawler crawler) {
        return null;
    }
}
