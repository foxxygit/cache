package com.foxxy.git.spring;

import net.sf.ehcache.Cache;

import com.foxxy.git.cache.CacheBroadcastListener;

public class CacheWrapper {

    private String broadcastListenerName;

    private net.sf.ehcache.Cache cache;

    private CacheBroadcastListener listener;

    private String awareClassName;

    public CacheWrapper() {

    }

    public CacheWrapper(Cache cache, String broadcastListenerName, String awareClassName) {
        super();
        this.cache = cache;
        this.broadcastListenerName = broadcastListenerName;
        this.awareClassName = awareClassName;
    }

    public String getBroadcastListenerName() {
        return broadcastListenerName;
    }

    public void setBroadcastListenerName(String broadcastListenerName) {
        this.broadcastListenerName = broadcastListenerName;
    }

    public CacheBroadcastListener getListener() {
        return listener;
    }

    public void setListener(CacheBroadcastListener listener) {
        this.listener = listener;
    }

    public net.sf.ehcache.Cache getCache() {
        return cache;
    }

    public void setCache(net.sf.ehcache.Cache cache) {
        this.cache = cache;
    }

    public String getAwareClassName() {
        return awareClassName;
    }

    public void setAwareClassName(String awareClassName) {
        this.awareClassName = awareClassName;
    }

}
