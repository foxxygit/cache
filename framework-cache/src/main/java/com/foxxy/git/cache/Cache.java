package com.foxxy.git.cache;

/**
 * 缓存操作抽象类接口，提供了缓存的基本操作<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface Cache {

    Object get(String key) throws CacheException;

    <T> T get(String key, Class<T> requiredType) throws CacheException;

    void set(String key, Object value) throws CacheException;

    void setonExpire(String key, Object value, int seconds) throws CacheException;

    void evict(String key) throws CacheException;

    void clear() throws CacheException;

    void destroy() throws CacheException;

    /**
     * 
     * 功能描述: 获取缓存广播的Listener 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    CacheBroadcastListener getCacheBroadcastListener();

    /**
     * 
     * 功能描述: 获取序列化类的声明 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String getSerialAwareClassName();

}
