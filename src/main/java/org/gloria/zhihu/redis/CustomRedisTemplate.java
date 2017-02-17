package org.gloria.zhihu.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gloria.zhihu.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2016/12/8 11:30.
 *
 * @author : gloria.
 */
@Service
public class CustomRedisTemplate {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
    }

    /**
     * 根据队列名称rpop出一个对象
     * @param key 队列名称
     * @param clazz 对象类型
     * @param <T>
     * @return
     */
    public <T> T rpop(String key, Class<T> clazz) {
        String json = redisTemplate.opsForList().rightPop(key);

        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据队列名称lpop出一个对象
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T lpop(String key, Class<T> clazz) {
        String json = redisTemplate.opsForList().leftPop(key);

        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 向指定队列中存储对象
     * @param key   队列名称
     * @param obj   对象
     */
    public void lpush(String key, Object obj) {
        try {
            redisTemplate.opsForList().leftPush(key, mapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向指定队列中存储对象
     * @param key 队列名称
     * @param obj 对象
     */
    public void rpush(String key, Object obj) {
        try {
            redisTemplate.opsForList().rightPush(key, mapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按照指定范围查出多个对象
     * @param key 队列名称
     * @param start start
     * @param end   end
     * @param clazz 对象类型
     * @param <T>
     * @return
     */
    public <T> List<T> range(String key, int start, int end, Class<T> clazz) {
        List<String> jsonList = redisTemplate.opsForList().range(key, start, end);

        List<T> objList = new ArrayList<T>();
        for (String s : jsonList) {
            try {
                objList.add(mapper.readValue(s, clazz));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return objList;
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public boolean setIfAbsent(String key, String value) {
        String md5 = Md5Util.generateMd5ByUrl(key);
        //已存在->false，不存在true
        boolean r = redisTemplate.opsForValue().setIfAbsent(key + ":" + md5, value);
        return r;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

}
