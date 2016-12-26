package org.gloria.zhihu.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.List;

/**
 * Create on 2016/12/7 22:04.
 *
 * @author : gloria.
 */
public abstract class CustomMongoTemplate {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存一个对象
     *      如该对象为已存在的对象则会更新，不会进行插入操作
     * @param obj
     * @param <T>
     */
    public <T> void save(T obj) {
        mongoTemplate.save(obj);
    }

    /**
     * 批量插入
     * @param collection
     * @param <T>
     */
    public <T> void batchSave(Collection<T> collection) {
        mongoTemplate.insert(collection);
    }

    /**
     * 根据Query条件查询一个对象
     * @param query 查询条件
     * @param clazz 对象类型
     * @param <T>
     * @return
     */
    public <T> T findOne(Query query, Class<T> clazz) {
        return mongoTemplate.findOne(query, clazz);
    }

    /**
     * 查询多个对象
     * @param query 查询条件
     * @param clazz 对象类型
     * @param <T>
     * @return
     */
    public <T> List<T> find(Query query, Class<T> clazz) {
        return mongoTemplate.find(query, clazz);
    }

}
