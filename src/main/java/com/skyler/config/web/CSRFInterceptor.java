package com.skyler.config.web;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/18 at 下午11:30
 */
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSRFInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(CSRFInterceptor.class);
    private static final String REFERER = "Referer";
    private static final String ALL = "all";
    private static final String METHOD = "get";
    @Value("${my.spring.security.csrf.referer:all}")
    private String regex;

    public CSRFInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("get".equalsIgnoreCase(request.getMethod())) {
            return true;
        } else if (StringUtils.equalsIgnoreCase(this.regex, "all")) {
            return true;
        } else {
            String referer = (String)StringUtils.defaultIfBlank(request.getHeader("Referer"), "");
            Pattern pattern = Pattern.compile(this.regex, 2);
            referer = referer.replace("&#47;", "/");
            Matcher matcher = pattern.matcher(referer);
            if (matcher.find()) {
                return true;
            } else {
                String urlAndReferer = String.format("url:%s;referer:%s", request.getRequestURL().toString(), referer);
                log.info("x-csrf-attack:{}", urlAndReferer);
                response.setHeader("x-csrf-attack", urlAndReferer);
                return false;
            }
        }
    }
}
