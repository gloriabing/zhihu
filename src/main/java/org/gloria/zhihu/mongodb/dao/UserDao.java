package org.gloria.zhihu.mongodb.dao;

import org.bson.types.ObjectId;
import org.gloria.zhihu.model.User;
import org.gloria.zhihu.mongodb.CustomMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Create on 2016/12/26 16:31.
 *
 * @author : gloria.
 */
@Repository
public class UserDao extends CustomMongoTemplate {

    public void save(User user) {
        if (null == find(user.getUrl())) {
            super.save(user);
        }
    }

    public User find(String url) {
        Query query = Query.query(Criteria.where("url").is(url));
        return findOne(query, User.class);
    }
    
}
