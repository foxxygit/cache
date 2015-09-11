package com.foxxy.git.spring;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.foxxy.git.cache.CacheManager;

public class CacheManagerHolder {

    private ConcurrentMap<String, CacheManager> map = Maps.newConcurrentMap();

    private static CacheManagerHolder holder = null;

    public static synchronized CacheManagerHolder getInstance() {
        if (null == holder) {
            holder = new CacheManagerHolder();
        }
        return holder;
    }

    private CacheManagerHolder() {

    }

    public void putIfAbsent(String name, CacheManager manager) {
        map.putIfAbsent(name, manager);
    }

    public CacheManager get(String name) {
        return map.get(name);
    }
}
