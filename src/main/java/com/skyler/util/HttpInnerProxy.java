package com.skyler.util;

import org.apache.http.HttpEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyang on 17/4/17.
 * 对外请求代理类
 * 所有对外请求都需要使用本类
 */
public class HttpInnerProxy {

    public static String post(String url, HttpEntity entity, Map<String, String> headerMap) {
        String targetHost = StringUtil.getDomainFromUrl(url);
        if(WeChatInfo.request_url!=null){
            url = url.replace(targetHost, WeChatInfo.request_url);
            if(headerMap == null){
                headerMap = new HashMap<String, String>();
            }
            headerMap.put(WeChatInfo.header_referer, targetHost);
        }
        return HttpClientUtils.post(url, entity, headerMap);
    }


    public static void main(String[] args) {
        String url = "https://www.skyler.cn/api/v1/history.do";
        //System.out.println(post(url));
    }


}
