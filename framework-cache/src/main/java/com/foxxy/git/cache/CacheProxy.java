package com.foxxy.git.cache;

/**
 * 缓存操作代理操作类<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface CacheProxy {

    /**
     * 
     * 功能描述: 从缓存中获取对象 〈功能详细描述〉
     *
     * @param level 从哪个层级的缓存获取
     * @param name 缓存名称
     * @param key 缓存key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    ValueWrapper<Object> get(CacheLevel level, String name, String key);

    /**
     * 
     * 功能描述: 从缓存中获取对象 〈功能详细描述〉
     *
     * @param level 从哪个层级的缓存获取
     * @param resultClass 需要返回的类型
     * @param name 缓存名称
     * @param key 缓存key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    <T> ValueWrapper<T> get(CacheLevel level, Class<T> resultClass, String name, String key);

    /**
     * 
     * 功能描述: 将指定key指定值放到指定缓存中取 〈功能详细描述〉
     *
     * @param level 放到哪个层级
     * @param name 缓存名称
     * @param key 缓存key
     * @param value 缓存的对象值
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void set(CacheLevel level, String name, String key, Object value);

    /**
     * 
     * 功能描述: 将指定key指定值放到指定缓存中取 〈功能详细描述〉
     *
     * @param level 放到哪个层级
     * @param name 缓存名称
     * @param key 缓存key
     * @param value 缓存的对象值
     * @param seconds 缓存生效时间，单位是秒
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void setonExpire(CacheLevel level, String name, String key, Object value, int seconds);

    /**
     * 
     * 功能描述: 往一级缓存放不广播 〈功能详细描述〉
     *
     * @param name 缓存名称
     * @param key 缓存key
     * @param value 缓存的对象值
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void setFirstNotBroad(String name, String key, Object value);
    
    /**
     * 
     * 功能描述: 往一级缓存放不广播带失效时间 〈功能详细描述〉
     *
     * @param name 缓存名称
     * @param key 缓存key
     * @param value 缓存的对象值
     * @param seconds 缓存生效时间，单位是秒
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void setFirstNotBroadExpire(String name, String key, Object value,int seconds);

    /**
     * 
     * 功能描述: 移除key 〈功能详细描述〉
     *
     * @param level 移除哪个层级的缓存
     * @param name 缓存名称
     * @param key 缓存key
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void evict(CacheLevel level, String name, String key);

    /**
     * 
     * 功能描述: 容器销毁或者优雅停机时需要做的事情 〈功能详细描述〉
     *
     * @param level
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void stop(CacheLevel level);

    /**
     * 
     * 对返回值的包装<br>
     * 〈功能详细描述〉
     *
     * @author 15050977 xy
     * @see [相关类/方法]（可选）
     * @since [产品/模块版本] （可选）
     */
    interface ValueWrapper<T> {

        T getValue();

        CacheLevel getCacheLevel();
    }
}
