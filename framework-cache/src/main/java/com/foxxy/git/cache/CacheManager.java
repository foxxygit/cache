package com.foxxy.git.cache;

import org.apache.commons.lang3.StringUtils;

import com.foxxy.git.cache.ehcache.EhCacheProvider;
import com.foxxy.git.cache.redis.RedisCacheProvider;

/**
 * 
 * 封装了一级，二级的缓存管理器,对上层透明<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CacheManager {

    private String name;

    private CacheProvider level1_provider;

    private CacheProvider level2_provider;

    private String serializerName;

    private String cacheLevelFirstName;

    private String cacheLevelSecondName;

    public CacheManager(String name, String serializerName, String cacheLevelFirstName, String cacheLevelSecondName) {
        super();
        this.name = name;
        this.serializerName = serializerName;
        this.cacheLevelFirstName = cacheLevelFirstName;
        this.cacheLevelSecondName = cacheLevelSecondName;
        level1_provider = getProviderInstance(cacheLevelFirstName);
        level2_provider = getProviderInstance(cacheLevelSecondName);
    }

    public CacheManager() {

    }

    private CacheProvider getProviderInstance(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if ("ehcache".equalsIgnoreCase(value)) {
            return new EhCacheProvider(serializerName);
        } else if ("redis".equalsIgnoreCase(value)) {
            return new RedisCacheProvider(serializerName);
        }
        throw new IllegalArgumentException("not support this cache：" + value);
    }

    private Cache _GetCache(CacheLevel level, String cache_name) {
        return ((level.equals(CacheLevel.FIRST)) ? level1_provider : level2_provider).buildCache(cache_name);
    }

    public void shutdown(CacheLevel level) {
        ((level.equals(CacheLevel.FIRST)) ? level1_provider : level2_provider).stop();
    }

    /**
     * 获取缓存中的数据
     *
     * @param level
     * @param name
     * @param key
     * @return
     */
    public Object get(CacheLevel level, String name, String key) {
        if (name != null && key != null) {
            Cache cache = _GetCache(level, name);
            if (cache != null)
                return cache.get(key);
        }
        return null;
    }

    /**
     * 获取缓存中的数据
     *
     * @param <T>
     * @param level
     * @param resultClass
     * @param name
     * @param key
     * @return
     */
    public <T> T get(CacheLevel level, Class<T> resultClass, String name, String key) {
        if (name != null && key != null) {
            Cache cache = _GetCache(level, name);
            if (cache != null)
                return (T) cache.get(key,resultClass);
        }
        return null;
    }

    /**
     * 写入缓存
     *
     * @param level
     * @param name
     * @param key
     * @param value
     */
    public void set(CacheLevel level, String name, String key, Object value) {
        if (name != null && key != null && value != null) {
            Cache cache = _GetCache(level, name);
            if (cache != null)
                cache.set(key, value);
        }
    }

    /**
     * 写入缓存带失效时间
     *
     * @param level
     * @param name
     * @param key
     * @param value
     */
    public void setonExpire(CacheLevel level, String name, String key, Object value, int seconds) {
        if (name != null && key != null && value != null) {
            Cache cache = _GetCache(level, name);
            if (cache != null)
                cache.setonExpire(key, value, seconds);
        }
    }

    /**
     * 清除缓存中的某个数据
     *
     * @param level
     * @param name
     * @param key
     */
    public void evict(CacheLevel level, String name, String key) {
        if (name != null && key != null) {
            Cache cache = _GetCache(level, name);
            if (cache != null)
                cache.evict(key);
        }
    }

    /**
     * Clear the cache
     */
    public void clear(CacheLevel level, String name) throws CacheException {
        Cache cache = _GetCache(level, name);
        if (cache != null)
            cache.clear();
    }

    /**
     * 功能描述: 获取缓存实例 〈功能详细描述〉
     *
     * @param level
     * @param cache_name
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public Cache getCache(CacheLevel level, String cache_name) {
        return _GetCache(level, cache_name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCacheLevelFirstName(String cacheLevelFirstName) {
        level1_provider = getProviderInstance(cacheLevelFirstName);
        this.cacheLevelFirstName = cacheLevelFirstName;
    }

    public void setCacheLevelSecondName(String cacheLevelSecondName) {
        level2_provider = getProviderInstance(cacheLevelSecondName);
        this.cacheLevelSecondName = cacheLevelSecondName;
    }

    public void setSerializerName(String serializerName) {
        this.serializerName = serializerName;
    }

    public String getCacheLevelFirstName() {
        return cacheLevelFirstName;
    }

    public String getCacheLevelSecondName() {
        return cacheLevelSecondName;
    }

}
