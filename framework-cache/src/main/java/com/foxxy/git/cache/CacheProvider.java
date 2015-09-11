package com.foxxy.git.cache;

import com.foxxy.git.coder.CacheCoder;

/**
 * 缓存提供者接口类，缓存可以是ehcache，redis等<br> 
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface CacheProvider {

    String name();

    Cache buildCache(String regionName) throws CacheException;

    void stop();

    CacheCoder getCacheCoder();

}
