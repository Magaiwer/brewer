package com.algaworks.brewer.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.cache.Caching;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@EnableAsync
@Configuration
@EnableCaching
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CacheManager cacheManager() throws Exception {
        return new JCacheCacheManager(Caching.getCachingProvider().getCacheManager(
                getClass().getResource("/env/ehcache.xml").toURI(),
                getClass().getClassLoader()));
    }



}
