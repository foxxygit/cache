package com.foxxy.git.cache;

/**
 * 缓存从ehcache中失效之后触发一个事件监听器<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface CacheExpiredListener {

    /**
     * 
     * 功能描述: 接受指定cacheName的key 〈功能详细描述〉
     * @param cacheName
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    boolean accept(String cacheName, Object key);

    /**
     * 
     * 功能描述: key失效之后触发的逻辑，如重新查库放到ehcache中
     * 〈功能详细描述〉
     *
     * @param cacheName 缓存名字
     * @param key 缓存key
     * @param value 缓存值
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void onKeyExpired(String cacheName, Object key, Object value);
}
