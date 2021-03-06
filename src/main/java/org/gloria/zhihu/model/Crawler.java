package org.gloria.zhihu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;

/**
 * Create on 2016/12/23 15:11.
 *
 * @author : gloria.
 */
@Setter
@Getter
@ToString
public class Crawler {

    private URI uri;
    private CrawlType crawlType;
    
    private String params;
    
}
