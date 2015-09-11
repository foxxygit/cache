package com.foxxy.git.spring;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.foxxy.git.cache.CacheManager;

public class CacheManagerBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    /**
     * element 相当于对应的element元素 parserContext 解析的上下文 builder 用于该标签的实现
     */
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        String id = element.getAttribute("id").trim();
        // 从标签中取出对应的属性值
        String serializerName = element.getAttribute("serializerName").trim();
        String name = element.getAttribute("name").trim();
        List<Element> elements = DomUtils.getChildElementsByTagName(element, "cache-levelFirst");
        String cacheLevelFirstName = null;
        if (CollectionUtils.isNotEmpty(elements)) {
            cacheLevelFirstName = elements.get(0).getAttribute("name").trim();
        }
        elements = DomUtils.getChildElementsByTagName(element, "cache-levelSecond");

        String cacheLevelSecondName = null;
        if (CollectionUtils.isNotEmpty(elements)) {
            cacheLevelSecondName = elements.get(0).getAttribute("name").trim();
        }
        CacheManager cacheManager = new CacheManager(name, serializerName, cacheLevelFirstName, cacheLevelSecondName);
        CacheManagerHolder.getInstance().putIfAbsent(name, cacheManager);
        CacheManagerHolder.getInstance().putIfAbsent("_default", cacheManager);
        builder.addPropertyValue("name", name);
        builder.addPropertyValue("serializerName", serializerName);
        builder.addPropertyValue("cacheLevelFirstName", cacheLevelFirstName);
        builder.addPropertyValue("cacheLevelSecondName", cacheLevelSecondName);

        // Spring框架必须要求有一个id属性
        // 把bean注入到spring容器中
        BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(builder.getRawBeanDefinition(), id),
                parserContext.getRegistry());
        // 把bean注入到spring容器中
        BeanDefinitionReaderUtils.registerBeanDefinition(
                new BeanDefinitionHolder(builder.getRawBeanDefinition(), name), parserContext.getRegistry());

    }

    @Override
    protected Class<CacheManager> getBeanClass(Element element) {
        // 返回该标签所定义的类实现,在这里是为了创建出CacheManager对象
        return CacheManager.class;
    }

}
