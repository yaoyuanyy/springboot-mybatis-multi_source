package com.skyler.config.web;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;
import java.util.Locale;

/**
 * spring mvc 配置类
 *
 * @author
 * @date 2017-04-11
 **/
@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(messageConverter());
    }

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public FastJsonHttpMessageConverter4 messageConverter() {
        final FastJsonHttpMessageConverter4 converter = new FastJsonHttpMessageConverter4();
        final FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteBigDecimalAsPlain);
//        final ValueFilter nullValueFilter = new NullValueFilter();
//        fastJsonConfig.setSerializeFilters(nullValueFilter);
        converter.setFastJsonConfig(fastJsonConfig);
        return converter;
    }

    @Bean
    public LocaleResolver localeResolver() {
        final CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        //保存7天有效
        localeResolver.setLanguageTagCompliant(true);
        localeResolver.setCookieMaxAge(604800);
        localeResolver.setDefaultLocale(Locale.CHINA);
        localeResolver.setCookieName("locale");
        localeResolver.setCookiePath("/");
        return localeResolver;
    }
//
//    @Bean
//    public CSRFInterceptor csrfInterceptor() {
//        return new CSRFInterceptor();
//    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return new MultiTerminalLocaleChangeInterceptor();
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        //registry.addInterceptor(csrfInterceptor());
        //registry.addInterceptor(localeChangeInterceptor());
    }

}
