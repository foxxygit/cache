package com.foxxy.git.cache.redis;

public class DefaultRedisCacheService implements CacheService{

	@Override
	public Object get(String key) {
		return null;
	}

	@Override
	public void del(String key) {
		
	}

	@Override
	public <T> T get(String key, Class<T> requiredType) {
		return null;
	}

	@Override
	public void set(String key, Object value) {
		
	}

	@Override
	public void setex(String key, Object value, int seconds) {
		
	}

}
