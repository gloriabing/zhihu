package org.gloria.zhihu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create on 2017/1/6 11:33.
 *
 * @author : gloria.
 */
public class RegexUtil {

    public static String regexMatch(String regexExp, String text) {
        Pattern pattern = Pattern.compile(regexExp);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
}
