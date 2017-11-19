package com.skyler.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhulei
 * @date 2017/8/31
 */
public class WebUtils {
    /**
     *  获取请求的客户端IP
     * @param request
     * @return String IP地址
     */
    public static String getClientIPAddress(HttpServletRequest request) {

        String address = request.getHeader("X-Real-IP");

        if (!StringUtils.isBlank(address)) {
            String[] ips = address.split(",");
            if (ips != null && ips.length > 0) {
                address = ips[0];
            }
            return address;
        }
        return request.getRemoteAddr();
    }
}