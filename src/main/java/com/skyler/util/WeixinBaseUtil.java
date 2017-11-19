package com.skyler.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bliam on 2017/3/17.
 *
 * @author bixiaofeng
 * @date 2017/03/17
 */
public class WeixinBaseUtil {

    private static final Logger log = LoggerFactory.getLogger(WeixinBaseUtil.class);

    private static StringRedisTemplate stringRedisTemplate;
    @Resource
    public static void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        WeixinBaseUtil.stringRedisTemplate = stringRedisTemplate;
    }

    public static final char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * jsApiTicket在memcached中的key
     */
    private static final String JS_TICKET_KEY = "weixin_jsapi_ticket2";
    /**
     * accessToken在memcached中的key
     */
    private static final String ACCESS_TOKEN_KEY = "weixin_access_token2";

    public static Map getJsSign(String url) {
        String accessToken = getAccessToken(ACCESS_TOKEN_KEY);
        return getJsSign(url, accessToken, JS_TICKET_KEY);
    }

    private static int TWO_HOURS = 2*60*60;

    private static final String monitorKey = "monitorAccessTokenKey2";

    public static String getAccessToken(String cachedKey){

        String url = WeChatInfo.acc_token_url;
        String accessToken = RedisUtil.get(cachedKey);
        Logs.getinfoLogger().info("access_token from redis:"+accessToken);
        if(StringUtils.isBlank(accessToken)){
            // 监控一下accessToken是不是正常2小时一次, 每天使用的次数
            String num = RedisUtil.get(monitorKey);
            if (StringUtils.isEmpty(num)) {
                num = "0";
                RedisUtil.set(monitorKey, num,24 * 3600, TimeUnit.SECONDS);
            }
            RedisUtil.incr(monitorKey);
            JSONObject json = getHttpReturn(url,null);
            accessToken = json.getString("access_token");
            log.info("access_token from wechat:{}", accessToken);
            RedisUtil.set(cachedKey, accessToken, TWO_HOURS, TimeUnit.SECONDS);
        }
        return accessToken;
    }

    /**
     * 获取jsApi接口签名相关信息
     * @return map
     */
    public static Map<String, String> getJsSign(String url, String accToken, String cacheKey){

        Map<String, String> map = new HashMap<String, String>();
        String sign = null;

        //step1  获取jsApi Ticket
        String jsTicket = RedisUtil.get(cacheKey);
        Logs.getinfoLogger().info("jsTicket from redis:"+jsTicket);
        if(StringUtils.isBlank(jsTicket)){
            String jsTicketUrl = WeChatInfo.js_token_url.replace("ACCESS_TOKEN", accToken);
            JSONObject json = getHttpReturn(jsTicketUrl, null);
            log.info("jsTicket json from wechat:{}", json);
            jsTicket = json.getString("ticket");
            Logs.getinfoLogger().info("new jsTicket:"+jsTicket);
            RedisUtil.set(cacheKey, jsTicket, TWO_HOURS, TimeUnit.SECONDS);
        }
        //step2  组成要生成签名的字符串
        String timeStamp = String.valueOf(System.currentTimeMillis()/1000);
        String nonceStr =  UUID.randomUUID().toString();
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("jsapi_ticket=")
                .append(jsTicket)
                .append("&noncestr=")
                .append(nonceStr)
                .append("&timestamp=")
                .append(timeStamp)
                .append("&url=")
                .append(url);

        //step3 SHA-1加密生成签名
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(sBuffer.toString().getBytes());
            sign = bytesToHexStr(digest);
            Logs.geterrorLogger().error("**微信**——JS-API生成的签名sign为： "+sign);

        } catch (NoSuchAlgorithmException e) {
            Logs.geterrorLogger().error("**微信**——获取JS-API接口信息出错", e);
        }

        map.put("appId", WeChatInfo.appId);
        map.put("timeStamp", timeStamp);
        map.put("nonceStr", nonceStr);
        map.put("sign", sign);

        return map;
    }
    private static String bytesToHexStr(byte[] bytePara){
        char [] tempChar = new char[2];
        StringBuffer sb = new StringBuffer();
        for(byte b : bytePara){
            tempChar[0] = Digit[(b >>> 4) & 0X0F];
            tempChar[1] = Digit[b & 0X0F];
            sb.append(new String(tempChar));
        }
        return sb.toString();
    }

    /**
     * 这个以后移到httpClientUtil中
     * http请求
     * @author zhanglin
     * @param url 请求的url地址
     * @param para body中传递的参数
     * @return
     */
    public static JSONObject getHttpReturn(String url, String para){
        JSONObject json = null;
        HttpEntity entity = null;
        if(para!=null){
            entity = new StringEntity(para,"utf-8");
        }
        String res = HttpInnerProxy.post(url,entity,null);
        log.info("data from wechat:{}",res);
        Logs.getinfoLogger().info("jsTicket from wechat:"+res);
        if(!HttpClientUtils.DEFAULT_RESPONSE.equals(res)) {
            json = JSON.parseObject(res);
        }

        return json;
    }
}
