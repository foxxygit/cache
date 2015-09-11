package com.foxxy.git.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service(value = "expiredListenerExecutor")
public class ExpiredListenerExecutor implements InitializingBean, ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(ExpiredListenerExecutor.class);

    private ApplicationContext applicationContext;

    private List<CacheExpiredListener> cacheExpiredListeners;

    public void executor(String cacheName, Object key, Object value) {
        if (CollectionUtils.isNotEmpty(cacheExpiredListeners)) {
            for (CacheExpiredListener cacheExpiredListener : cacheExpiredListeners) {
                if (cacheExpiredListener.accept(cacheName, key)) {
                    try {
                        cacheExpiredListener.onKeyExpired(cacheName, key, value);
                    } catch (Exception e) {
                        logger.error("invoke cacheExpiredListener failed :!!!!", cacheExpiredListener.getClass()
                                .getName(), e);
                    }
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cacheExpiredListeners = new ArrayList<CacheExpiredListener>(applicationContext.getBeansOfType(
                CacheExpiredListener.class).values());
    }
}
