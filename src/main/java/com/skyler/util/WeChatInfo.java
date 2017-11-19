package com.skyler.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/13 at 下午9:07
 */
@ConfigurationProperties(prefix = "wechat")
@Component
public class WeChatInfo {

    public static String appId;
    public static String secret;
    public static String acc_token_url;
    public static String js_token_url;
    /**
     * 所有外网请求的出口
     */
    public static String request_url;
    public static String header_referer;

    public static String getAppId() {
        return appId;
    }

    public static void setAppId(String appId) {
        WeChatInfo.appId = appId;
    }

    public static String getSecret() {
        return secret;
    }

    public static void setSecret(String secret) {
        WeChatInfo.secret = secret;
    }

    public static String getAcc_token_url() {
        return acc_token_url;
    }

    public static void setAcc_token_url(String acc_token_url) {
        WeChatInfo.acc_token_url = acc_token_url;
    }

    public static String getJs_token_url() {
        return js_token_url;
    }

    public static void setJs_token_url(String js_token_url) {
        WeChatInfo.js_token_url = js_token_url;
    }

    public static String getRequest_url() {
        return request_url;
    }

    public static void setRequest_url(String request_url) {
        WeChatInfo.request_url = request_url;
    }

    public static String getHeader_referer() {
        return header_referer;
    }

    public static void setHeader_referer(String header_referer) {
        WeChatInfo.header_referer = header_referer;
    }

}
