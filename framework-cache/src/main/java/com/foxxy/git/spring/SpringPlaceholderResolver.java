package com.foxxy.git.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.ReflectionUtils;

/**
 * 将所有通过PropertyPlaceholderConfigurer加载的properties文件合并提供给业务使用里面的key值<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SpringPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver, InitializingBean,
        ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Properties p = new Properties();

    public void init() {
        List<PropertyPlaceholderConfigurer> collections = new ArrayList<PropertyPlaceholderConfigurer>(
                applicationContext.getBeansOfType(PropertyPlaceholderConfigurer.class).values());
        if (CollectionUtils.isNotEmpty(collections)) {
            for (PropertyPlaceholderConfigurer propertyPlaceholderConfigurer : collections) {
                Properties result = invokeMergeProperty(propertyPlaceholderConfigurer);
                if (null != result && !result.isEmpty()) {
                    p.putAll(result);
                }
            }
        }
    }

    private Properties invokeMergeProperty(PropertyPlaceholderConfigurer propertyPlaceholderConfigurer) {
        Properties p = null;
        try {
            Method m = PropertiesLoaderSupport.class.getDeclaredMethod("mergeProperties", (Class[]) null);
            ReflectionUtils.makeAccessible(m);
            p = (Properties) ReflectionUtils.invokeMethod(m, propertyPlaceholderConfigurer);
        } catch (Exception e) {
            // donothing
        }
        return p;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        return p.getProperty(placeholderName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
