package com.foxxy.git.cache.ehcache;

import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.CacheManager;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxxy.git.cache.CacheBroadcastListener;
import com.foxxy.git.cache.CacheException;
import com.foxxy.git.cache.CacheProvider;
import com.foxxy.git.coder.CacheCoder;
import com.foxxy.git.context.SpringContext;
import com.foxxy.git.spring.CacheWrapper;
import com.foxxy.git.spring.EhCacheWrapperHolder;

/**
 * ehcache缓存提供类<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class EhCacheProvider implements CacheProvider {

    private final static Logger log = LoggerFactory.getLogger(EhCacheProvider.class);

    private ConcurrentHashMap<String, EhCache> _CacheManager = new ConcurrentHashMap<String, EhCache>();

    public EhCacheProvider(String serializerName) {
        super();
    }

    @Override
    public String name() {
        return "ehcache";
    }

    @Override
	public EhCache buildCache(String name) throws CacheException {
        EhCache ehcache = _CacheManager.get(name);
        if (ehcache == null) {
            try {
                synchronized (_CacheManager) {
                    CacheWrapper cache = EhCacheWrapperHolder.getInstance().get(name);
                    if (cache == null) {
                        log.warn("cache not exsit cache name", name);
                        throw new CacheException("cache not exsit cache name:" + name);
                    }
                    ehcache = new EhCache(cache.getCache(), SpringContext.getBean(cache.getBroadcastListenerName(),
                            CacheBroadcastListener.class),cache.getAwareClassName());
                    _CacheManager.put(name, ehcache);
                }
            } catch (net.sf.ehcache.CacheException e) {
                throw new CacheException(e);
            }
        }
        return ehcache;
    }

    @Override
	public void stop() {
        if (MapUtils.isNotEmpty(_CacheManager)) {
            _CacheManager.clear();
        }
        CacheManager.getInstance().shutdown();
    }

    @Override
    public CacheCoder getCacheCoder() {
        // ehcache not support cutom Coder;
        return null;
    }

}
