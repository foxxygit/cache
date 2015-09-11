package com.foxxy.git.cache;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.foxxy.git.spring.EhCacheWrapperHolder;
import com.foxxy.git.utils.NamedThreadFactory;

/**
 * ehcache缓存命中率统计的task<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class StatisticTask implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(StatisticTask.class);

    private ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new NamedThreadFactory(
            "cache-StatisticTask", true));

    private static final Integer INITDELAY = 180;

    /**
     * 10s执行一次
     */
    private static final Integer PERIOD = 10;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 定时统计
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    printCacheStatistic();
                } catch (Throwable e) {
                    logger.error("print cache statistic failed!!", e);
                }
            }// 首次启动4分钟后开始执行，每隔10s执行一次
        }, INITDELAY, PERIOD, TimeUnit.SECONDS);
    }

    private void printCacheStatistic() {
        List<net.sf.ehcache.Cache> caches = EhCacheWrapperHolder.getInstance().getCaches();
        if (CollectionUtils.isEmpty(caches)) {
            return;
        }
        // bytes
        for (net.sf.ehcache.Cache cache : caches) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("cacheName: ").append(cache.getName()).append(" cacheSize:").append(cache.getSize())
                    .append(" cache-memory:").append(cache.getMemoryStoreSize()).append(" cache-heap-size:")
                    .append(cache.calculateInMemorySize());
            // 是否开启了命中率统计功能
            if (cache.isStatisticsEnabled()) {
                buffer.append(" cache-hits:").append(cache.getStatistics().getCacheHits()).append(" cache-misses:")
                        .append(cache.getStatistics().getCacheMisses()).append("\n");
            }
            if (logger.isInfoEnabled()) {
                logger.info(buffer.toString());
            }
        }
    }
}
