package com.foxxy.git.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CacheNamespaceHandlerSupport extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("cacheManager", new CacheManagerBeanDefinitionParser());
        registerBeanDefinitionParser("cache", new CacheBeanDefinitionParser());
    }

}
