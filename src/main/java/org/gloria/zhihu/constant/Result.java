package org.gloria.zhihu.constant;

import lombok.Getter;

/**
 * Create on 2016/12/28 15:49.
 *
 * @author : gloria.
 */
@Getter
public enum  Result {
    
    SUCCESS(200),
    NOT_FOUND(404),
    ERROR(500);

    private int code;

    private Result() {
        
    }
    private Result(int code) {
        this.code = code;
    }
    
    
}
