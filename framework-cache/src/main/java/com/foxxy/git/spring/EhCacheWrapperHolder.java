package com.foxxy.git.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class EhCacheWrapperHolder {

    private ConcurrentMap<String, CacheWrapper> map = Maps.newConcurrentMap();

    private static EhCacheWrapperHolder holder = null;

    public static synchronized EhCacheWrapperHolder getInstance() {
        if (null == holder) {
            holder = new EhCacheWrapperHolder();
        }
        return holder;
    }

    private EhCacheWrapperHolder() {

    }

    public void putIfAbsent(String name, CacheWrapper wrapper) {
        map.putIfAbsent(name, wrapper);
    }

    public CacheWrapper get(String name) {
        return map.get(name);
    }

    public List<net.sf.ehcache.Cache> getCaches() {
        List<net.sf.ehcache.Cache> caches = new ArrayList<net.sf.ehcache.Cache>(map.size());
        for (String key : map.keySet()) {
            CacheWrapper wrapper = map.get(key);
            if (null != wrapper.getCache()) {
                caches.add(wrapper.getCache());
            }
        }
        return caches;
    }
}
