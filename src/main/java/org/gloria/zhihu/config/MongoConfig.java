package org.gloria.zhihu.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Created by gloria on 2016/12/6.
 */
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String uri;


    @Bean
    MongoClientURI baseMongoClientURI() {
        return new MongoClientURI(uri);
    }

    @Bean
    MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(baseMongoDbFactory(), baseMongoConverter());
    }

    @Bean
    MongoDbFactory baseMongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(baseMongoClientURI()), baseMongoClientURI().getDatabase());
    }

    @Bean
    MappingMongoConverter baseMongoConverter() throws Exception {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(baseMongoDbFactory()), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }
}
