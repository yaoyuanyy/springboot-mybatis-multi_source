package com.skyler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/13 at 下午7:04
 */
public class StringUtil {

    /**
     * 从url中获得host
     * @param url
     * @return
     */
    public static String getDomainFromUrl(String url) {
        String regEx = "(https?://[^/]+)/.*";
        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static boolean isEmpty(String str) {
        if(str == null)
            return true;
        String tempStr = str.trim();
        if(tempStr.length() == 0)
            return true;
        if(tempStr.equals("null"))
            return true;
        return false;
    }
}
