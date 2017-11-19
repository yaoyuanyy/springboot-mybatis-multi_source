package com.skyler.util;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/18 at 下午11:28
 */
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class LocaleUtils {
    private static final Logger log = LoggerFactory.getLogger(LocaleUtils.class);
    public static final String SPIDER = "spider";
    public static final String ANDROID = "android";
    public static final String IPHONE = "iphone";
    public static final String IOS = "ios";
    public static final String USER_AGENT = "User-Agent";
    public static final String[] ENGLISH = new String[]{"en-us", "en_us"};
    public static final String[] CHINA = new String[]{"zh-cn", "zh_cn"};
    public static final String[] CHINA_TW = new String[]{"zh-tw", "zh_tw"};
    public static final String[] CHINA_HK = new String[]{"zh-hk", "zh_hk"};
    public static final String[] TRADITIONAL_CHINESE = new String[]{"zh-tw", "zh_tw", "zh-hk", "zh_hk"};
    private static final char UNDERLINE = '_';
    private static final char DASH = '-';
    private static LocaleResolver localeResolver;
    private static MessageSource messageSource;

    @Autowired
    public LocaleUtils(LocaleResolver localeResolver, MessageSource messageSource) {
        localeResolver = localeResolver;
        messageSource = messageSource;
    }

    public static void setInitLocale(HttpServletRequest request, HttpServletResponse response) {
        Locale locale = request.getLocale();
        if (localeResolver instanceof CookieLocaleResolver) {
            CookieLocaleResolver cookieLocaleResolver = (CookieLocaleResolver)localeResolver;
            Cookie cookie = WebUtils.getCookie(request, cookieLocaleResolver.getCookieName());
            if (cookie == null) {
                setLocale(request, response, locale, "Init CookieLocaleResolver locale url :{},country:{},lang:{}");
                return;
            }

            log.debug("CookieLocaleResolver locale name:{} ,value:{}", cookie.getName(), cookie.getValue());
        }

        if (localeResolver instanceof SessionLocaleResolver) {
            Locale sessionLocale = (Locale)WebUtils.getRequiredSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            if (sessionLocale == null) {
                setLocale(request, response, locale, "Init SessionLocaleResolver locale url :{}, country:{},lang:{}");
                return;
            }

            log.debug("SessionLocaleResolver Locale: {}", sessionLocale.toLanguageTag());
        }

    }

    private static void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale, String format) {
        setLocale(locale.toString(), request, response);
        log.debug(format, new Object[]{request.getRequestURL().toString(), locale.getCountry(), locale.toLanguageTag()});
    }

    public static void setLocale(String lang, HttpServletRequest request, HttpServletResponse response) {
        lang = ((String)StringUtils.defaultIfEmpty(lang, Locale.CHINA.toString())).toLowerCase();
        if (StringUtils.containsAny(lang, ENGLISH)) {
            localeResolver.setLocale(request, response, Locale.US);
        } else if (StringUtils.containsAny(lang, CHINA_TW)) {
            localeResolver.setLocale(request, response, Locale.TRADITIONAL_CHINESE);
        } else if (StringUtils.containsAny(lang, CHINA_HK)) {
            localeResolver.setLocale(request, response, new Locale("zh", "HK"));
        } else {
            localeResolver.setLocale(request, response, Locale.CHINA);
        }

    }

    public static void setLocale(HttpServletRequest request, HttpServletResponse response) {
        String userAgent = ((String)StringUtils.defaultIfBlank(request.getHeader("User-Agent"), "")).toLowerCase();
        log.debug("USER_AGENT :{}", userAgent);
        if (StringUtils.containsAny(userAgent, new CharSequence[]{"spider", "android", "ios", "iphone"})) {
            setLocale(userAgent, request, response);
        } else {
            setInitLocale(request, response);
        }

    }

    public static String getMessage(String code) {
        return getMessage(code, (Object[])null);
    }

    public static String getMessage(String code, Object[] args) {
        return getMessage(code, args, "");
    }

    public static String getMessage(String code, Object[] args, String defaultMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        log.debug("Message Locale tag:{},value:{}", locale.toLanguageTag(), locale.toString());
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public static Map<String, String> getMessages(String... codePrefixes) {
        Locale locale = LocaleContextHolder.getLocale();
        return ((CustomResourceBundleMessageSource)messageSource).getMessages(locale, codePrefixes);
    }

    public static String getLocale(HttpServletRequest request) {
        return getLocale(request, "locale");
    }

    public static String getLocale(HttpServletRequest request, String cookieName) {
        Cookie localeCookie = WebUtils.getCookie(request, cookieName);
        String lang = Locale.SIMPLIFIED_CHINESE.toString();
        return localeCookie != null ? StringUtils.replaceChars(localeCookie.getValue(), '-', '_') : lang;
    }

    public static boolean isTraditionalChinese(String lang) {
        return StringUtils.equalsAnyIgnoreCase(lang, TRADITIONAL_CHINESE);
    }

    class CustomResourceBundleMessageSource extends ResourceBundleMessageSource {
        public CustomResourceBundleMessageSource() {
        }

        public Map<String, String> getMessages(Locale locale, String... codePrefixes) {
            Map<String, String> messagesMap = new HashMap(128);
            if (ArrayUtils.isEmpty(codePrefixes)) {
                return messagesMap;
            } else {
                Set<String> basenames = this.getBasenameSet();
                Iterator var5 = basenames.iterator();

                while(true) {
                    ResourceBundle bundle;
                    do {
                        if (!var5.hasNext()) {
                            return messagesMap;
                        }

                        String basename = (String)var5.next();
                        bundle = this.getResourceBundle(basename, locale);
                    } while(bundle == null);

                    Iterator var8 = bundle.keySet().iterator();

                    while(var8.hasNext()) {
                        String key = (String)var8.next();
                        if (StringUtils.startsWithAny(key, codePrefixes)) {
                            messagesMap.put(key, bundle.getString(key));
                        }
                    }
                }
            }
        }
    }
}
