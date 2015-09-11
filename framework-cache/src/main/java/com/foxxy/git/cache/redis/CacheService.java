package com.foxxy.git.cache.redis;

public interface CacheService {

	Object get(String key);

	void del(String key);

	<T> T get(String key, Class<T> requiredType);

	void set(String key, Object value);

	void setex(String key, Object value, int seconds);
}
