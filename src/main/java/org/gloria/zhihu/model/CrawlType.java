package org.gloria.zhihu.model;

/**
 * Create on 2016/12/23 15:12.
 *
 * @author : gloria.
 */
public enum CrawlType {

    SEED(-1),
    
    CATALOG(0),
    
    CONTENT(1),
    
    QUESTION(20),
    Zhuanlan(21),
    
    ANSWER(3),
    
    FOLLOWEES(100),

    FOLLOWERS(200);

    int type;

    private CrawlType() {
        
    }


    private CrawlType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
