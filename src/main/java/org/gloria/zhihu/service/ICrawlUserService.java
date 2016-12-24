package org.gloria.zhihu.service;

import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.User;

import java.util.List;

/**
 * Create on 2016/12/23 15:10.
 *
 * @author : gloria.
 */
public interface ICrawlUserService {

    List<Crawler> parseCatalog(Crawler crawler);

    User parseContent(Crawler crawler);
    
}
