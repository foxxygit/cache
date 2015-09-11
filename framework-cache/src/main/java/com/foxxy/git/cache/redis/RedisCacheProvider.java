package com.foxxy.git.cache.redis;

import java.util.concurrent.ConcurrentHashMap;

import com.foxxy.git.cache.Cache;
import com.foxxy.git.cache.CacheException;
import com.foxxy.git.cache.CacheProvider;
import com.foxxy.git.coder.CacheCoder;
import com.foxxy.git.coder.DefaultJsonCacheCoder;
import com.foxxy.git.context.SpringContext;

/**
 * 
 * Redis 缓存提供者<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RedisCacheProvider implements CacheProvider {

    private String serializerName;

    private ConcurrentHashMap<String, RedisCache> _CacheManager = new ConcurrentHashMap<String, RedisCache>();

    private final Object lockObj = new Object();

    public RedisCacheProvider(String serializerName) {
        super();
        this.serializerName = serializerName;
    }

    @Override
    public String name() {
        return "redis";
    }

    @Override
    public Cache buildCache(String regionName) throws CacheException {
        RedisCache cache = _CacheManager.get(regionName);
        synchronized (lockObj) {
            if (null == cache) {
                cache = new RedisCache(regionName,getCacheCoder());
            }
            _CacheManager.putIfAbsent(regionName, cache);
        }
        return cache;
    }

    @Override
    public void stop() {
        // donothing
    }

    @Override
    public CacheCoder getCacheCoder() {
        if ("json".equals(serializerName)) {
            return SpringContext.getBean("defaultJsonCacheCoder", DefaultJsonCacheCoder.class);
        }
        // 默认
        return SpringContext.getBean("defaultJsonCacheCoder", DefaultJsonCacheCoder.class);
    }
}
