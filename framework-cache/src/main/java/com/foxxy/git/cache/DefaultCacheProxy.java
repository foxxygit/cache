package com.foxxy.git.cache;

import org.springframework.util.Assert;

import com.foxxy.git.cache.server.NotifyService;
import com.foxxy.git.context.SpringContext;
import com.foxxy.git.spring.CacheManagerHolder;

/**
 * 默认缓存操作代理操作类〉<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DefaultCacheProxy implements CacheProxy {

    private CacheManager manager;

    private DefaultCacheProxy() {

    }

    private DefaultCacheProxy(String name) {
        this.manager = CacheManagerHolder.getInstance().get(name);
        Assert.notNull(manager);
    }

    public static CacheProxy proxy(String name) {
        return new DefaultCacheProxy(name);
    }

    public static CacheProxy proxy() {
        return new DefaultCacheProxy("_default");
    }

    @Override
    public ValueWrapper<Object> get(CacheLevel level, String name, String key) {

        if (level.equals(CacheLevel.FIRST)) {
            Object obj = manager.get(level, name, key);
            if (null != obj) {
                return new SimpleValueWrapper<Object>(obj, CacheLevel.FIRST);
            }
            return new NullValueWrapper<Object>();
        }
        if (level.equals(CacheLevel.SENCOND)) {
            Object obj = manager.get(level, name, key);
            if (null != obj) {
                return new SimpleValueWrapper<Object>(obj, CacheLevel.SENCOND);
            }
            return new NullValueWrapper<Object>();
        }
        if (level.equals(CacheLevel.ALL)) {
            Object obj = manager.get(CacheLevel.FIRST, name, key);
            if (null != obj) {
                return new SimpleValueWrapper<Object>(obj, CacheLevel.FIRST);
            }
            obj = manager.get(CacheLevel.SENCOND, name, key);
            if (null != obj) {
                return new SimpleValueWrapper<Object>(obj, CacheLevel.SENCOND);
            }
            return new NullValueWrapper<Object>();
        }
        throw new CacheException("unsupport this CacheLevel:" + level);
    }

    @Override
    public <T> ValueWrapper<T> get(CacheLevel level, Class<T> resultClass, String name, String key) {
        if (level.equals(CacheLevel.FIRST)) {
            T t = manager.get(level, resultClass, name, key);
            if (null != t) {
                return new SimpleValueWrapper<T>(t, CacheLevel.FIRST);
            }
            return new NullValueWrapper<T>();
        }
        if (level.equals(CacheLevel.SENCOND)) {
            T t = manager.get(level, resultClass, name, key);
            if (null != t) {
                return new SimpleValueWrapper<T>(t, CacheLevel.SENCOND);
            }
            return new NullValueWrapper<T>();
        }
        if (level.equals(CacheLevel.ALL)) {
            T t = manager.get(CacheLevel.FIRST, resultClass, name, key);
            if (null != t) {
                return new SimpleValueWrapper<T>(t, CacheLevel.FIRST);
            }
            t = manager.get(CacheLevel.SENCOND, resultClass, name, key);
            if (null != t) {
                return new SimpleValueWrapper<T>(t, CacheLevel.SENCOND);
            }
            return new NullValueWrapper<T>();
        }
        throw new CacheException("unsupport this CacheLevel:" + level);
    }

    @Override
    public void set(CacheLevel level, String name, String key, Object value) {
        if (level.equals(CacheLevel.FIRST)) {
            manager.set(level, name, key, value);
            // 将修改广播出去
            SpringContext.getBean("defaultNotifyService", NotifyService.class).notifyElementChange(
                    new ElementWrapper(ActionEventKey.UPDATE, manager.getName(), name, key, value, null, null));
        } else if (level.equals(CacheLevel.SENCOND)) {
            manager.set(level, name, key, value);
        } else if (level.equals(CacheLevel.ALL)) {
            manager.set(CacheLevel.FIRST, name, key, value);
            manager.set(CacheLevel.SENCOND, name, key, value);
            // 将修改广播出去
            SpringContext.getBean("defaultNotifyService", NotifyService.class).notifyElementChange(
                    new ElementWrapper(ActionEventKey.UPDATE, manager.getName(), name, key, value, null, null));
        } else {
            throw new CacheException("unsupport this CacheLevel:" + level);
        }
    }

    @Override
    public void setonExpire(CacheLevel level, String name, String key, Object value, int seconds) {
        if (level.equals(CacheLevel.FIRST)) {
            manager.setonExpire(level, name, key, value, seconds);
            // 将修改广播出去
            SpringContext.getBean("defaultNotifyService", NotifyService.class).notifyElementChange(
                    new ElementWrapper(ActionEventKey.UPDATE, manager.getName(), name, key, value, null, seconds));
        } else if (level.equals(CacheLevel.SENCOND)) {
            manager.setonExpire(level, name, key, value, seconds);
        } else if (level.equals(CacheLevel.ALL)) {
            manager.setonExpire(CacheLevel.FIRST, name, key, value, seconds);
            manager.setonExpire(CacheLevel.SENCOND, name, key, value, seconds);
            // 将修改广播出去
            SpringContext.getBean("defaultNotifyService", NotifyService.class).notifyElementChange(
                    new ElementWrapper(ActionEventKey.UPDATE, manager.getName(), name, key, value, null, seconds));
        } else {
            throw new CacheException("unsupport this CacheLevel:" + level);
        }
    }

    @Override
    public void evict(CacheLevel level, String name, String key) {
        if (level.equals(CacheLevel.FIRST)) {
            manager.evict(level, name, key);
            // 将修改广播出去
            SpringContext.getBean("defaultNotifyService", NotifyService.class).notifyElementChange(
                    new ElementWrapper(ActionEventKey.DELETE, manager.getName(), name, key, null, null, null));
        } else if (level.equals(CacheLevel.SENCOND)) {
            manager.evict(level, name, key);
        } else if (level.equals(CacheLevel.ALL)) {
            manager.evict(CacheLevel.FIRST, name, key);
            manager.evict(CacheLevel.SENCOND, name, key);
            // 将修改广播出去
            SpringContext.getBean("defaultNotifyService", NotifyService.class).notifyElementChange(
                    new ElementWrapper(ActionEventKey.DELETE, manager.getName(), name, key, null, null, null));
        } else {
            throw new CacheException("unsupport this CacheLevel:" + level);
        }
    }

    @Override
    public void setFirstNotBroad(String name, String key, Object value) {
        manager.set(CacheLevel.FIRST, name, key, value);
    }

    @Override
    public void setFirstNotBroadExpire(String name, String key, Object value, int seconds) {
        manager.setonExpire(CacheLevel.FIRST, name, key, value, seconds);
    }

    @Override
    public void stop(CacheLevel level) {
        manager.shutdown(level);
    }

}
