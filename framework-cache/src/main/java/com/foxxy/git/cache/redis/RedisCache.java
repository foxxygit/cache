package com.foxxy.git.cache.redis;

import com.foxxy.git.cache.Cache;
import com.foxxy.git.cache.CacheBroadcastListener;
import com.foxxy.git.cache.CacheException;
import com.foxxy.git.coder.CacheCoder;
import com.foxxy.git.context.SpringContext;

/**
 * 
 * Redis 缓存实现<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RedisCache implements Cache {

	private String region;

	private CacheCoder cacheCoder;

	private CacheService cacheService;

	public RedisCache(String region, CacheCoder cacheCoder) {
		this.cacheCoder = cacheCoder;
		this.region = region;
		this.cacheService = SpringContext.getBean("defaultCacheService",
				CacheService.class);
	}

	@Override
	public Object get(String key) throws CacheException {
		return cacheService.get(key);
	}

	@Override
	public void set(String key, Object value) throws CacheException {
		if (value == null)
			evict(key);
		else {
			if (value instanceof String) {
				cacheService.set(key, value);
			} else {
				cacheService.set(key, cacheCoder.encode(value));
			}
		}
	}

	@Override
	public void evict(String key) throws CacheException {
		cacheService.del(key);
	}

	@Override
	public void clear() throws CacheException {
		// donothing
	}

	@Override
	public void destroy() throws CacheException {
		this.clear();
	}

	public String getRegion() {
		return region;
	}

	@Override
	public <T> T get(String key, Class<T> requiredType) throws CacheException {
		return cacheService.get(key, requiredType);
	}

	@Override
	public CacheBroadcastListener getCacheBroadcastListener() {
		// no need CacheBroadcastListener
		return null;
	}

	@Override
	public void setonExpire(String key, Object value, int seconds)
			throws CacheException {
		if (value == null)
			evict(key);
		else {
			if (value instanceof String) {
				cacheService.setex(key, value, seconds);
			} else {
				cacheService.setex(key, cacheCoder.encode(value), seconds);
			}
		}
	}

	@Override
	public String getSerialAwareClassName() {
		// no need serialAwareClassName
		return null;
	}
}
