package com.foxxy.git.cache.ehcache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import com.foxxy.git.cache.ActionEventKey;
import com.foxxy.git.cache.Cache;
import com.foxxy.git.cache.CacheBroadcastListener;
import com.foxxy.git.cache.CacheException;
import com.foxxy.git.cache.ElementWrapper;
import com.foxxy.git.cache.ExpiredListenerExecutor;
import com.foxxy.git.context.SpringContext;

/**
 * ehcache提供类<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class EhCache implements Cache, CacheEventListener {

    private net.sf.ehcache.Cache cache;
    private CacheBroadcastListener listener;
    private String awareClassName;

    public EhCache(net.sf.ehcache.Cache cache, CacheBroadcastListener listener, String awareClassName) {
        this.cache = cache;
        this.cache.getCacheEventNotificationService().registerListener(this);
        this.listener = listener;
        this.awareClassName = awareClassName;
    }

    @Override
    public Object get(String key) throws CacheException {
        try {
            if (key == null) {
                return null;
            } else {
                Element element = cache.get(key);
                if (element != null)
                    return element.getObjectValue();
            }
            return null;
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void set(String key, Object value) throws CacheException {
        try {
            Element element = new Element(key, value);
            cache.put(element);
        } catch (IllegalArgumentException e) {
            throw new CacheException(e);
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }

    }

    @Override
    public void setonExpire(String key, Object value, int seconds) throws CacheException {
        try {
            Element element = new Element(key, value);
            element.setTimeToLive(seconds);
            cache.put(element);
        } catch (IllegalArgumentException e) {
            throw new CacheException(e);
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void evict(String key) throws CacheException {
        try {
            cache.remove(key);
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void clear() throws CacheException {
        try {
            cache.removeAll();
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void destroy() throws CacheException {
        try {
            cache.getCacheManager().removeCache(cache.getName());
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }

    @Override
	public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void notifyElementEvicted(Ehcache arg0, Element arg1) {
    }

    @Override
    public void notifyElementExpired(Ehcache cache, Element elem) {
        if (listener != null) {
            listener.notifyElementUpdate(new ElementWrapper(ActionEventKey.DELETE, "default", cache.getName(),
                    (String) elem.getObjectKey(), elem.getObjectValue(), null, null));
        }
        SpringContext.getBean("expiredListenerExecutor", ExpiredListenerExecutor.class).executor(cache.getName(),
                elem.getObjectKey(), elem.getObjectValue());
    }

    @Override
    public void notifyElementPut(Ehcache arg0, Element arg1) throws net.sf.ehcache.CacheException {
    }

    @Override
    public void notifyElementRemoved(Ehcache arg0, Element arg1) throws net.sf.ehcache.CacheException {
    }

    @Override
    public void notifyElementUpdated(Ehcache arg0, Element arg1) throws net.sf.ehcache.CacheException {
    }

    @Override
    public void notifyRemoveAll(Ehcache arg0) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> requiredType) throws CacheException {
        return (T) this.get(key);
    }

    @Override
    public CacheBroadcastListener getCacheBroadcastListener() {
        return listener;
    }

    @Override
    public String getSerialAwareClassName() {
        return awareClassName;
    }
}