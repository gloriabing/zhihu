package org.gloria.zhihu.service;

import org.gloria.zhihu.model.Crawler;
import org.gloria.zhihu.model.User;
import org.gloria.zhihu.model.UserInfo;

import java.util.List;

/**
 * Create on 2016/12/23 15:10.
 *
 * @author : gloria.
 */
public interface ICrawlUserService {

    List<Crawler> parseCatalog(Crawler crawler);

    User parseContent(Crawler crawler);

    UserInfo parseUserInfo(Crawler crawler);

    List<UserInfo> parseUserFollows(Crawler crawler);

}
