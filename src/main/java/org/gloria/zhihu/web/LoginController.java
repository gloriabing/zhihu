package org.gloria.zhihu.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create on 2016/12/28 15:46.
 *
 * @author : gloria.
 */
@RestController
public class LoginController {
    
    @RequestMapping("login")
    public Object login() {
        return null;    
    }
}
