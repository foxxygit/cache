package com.foxxy.git.cache;

import java.util.List;
import java.util.Set;

public interface CacheService {

    String set(String key, String value);

    String set(String key, Object obj);

    Long del(String key);

    <T> T get(String key, Class<T> clazz);

    String setex(String key, int sencods, String value);

    Long incr(String key);

    Boolean exists(String key);

    String get(String key);

    Long zadd(final String key, final double score, final String value);

    Long zrem(final String key, final String value);

    Double zscore(final String key, final String value);

    Set<String> zrange(final String key, final long start, final long end);

    Set<String> zrevrange(final String key, final long start, final long end);

    Long zcard(final String key);

    Long hincrBy(final String key, final String field, final long value);

    String hget(final String key, final String field);
    
    Long decr(final String key);
    
    Long incrBy(final String key, final long integer);
    
    Long decrBy(final String key, final long integer);
    
    List<String> pipLineGet(final String... keys);
}
