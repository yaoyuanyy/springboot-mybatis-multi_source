package com.skyler.config.web;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/18 at 下午11:27
 */

import com.skyler.util.LocaleUtils;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MultiTerminalLocaleChangeInterceptor extends LocaleChangeInterceptor {
    public MultiTerminalLocaleChangeInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        LocaleUtils.setLocale(request, response);
        return true;
    }
}
