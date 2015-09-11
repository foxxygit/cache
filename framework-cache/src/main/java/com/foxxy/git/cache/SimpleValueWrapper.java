package com.foxxy.git.cache;

import com.foxxy.git.cache.CacheProxy.ValueWrapper;

public class SimpleValueWrapper<T> implements ValueWrapper<T>{
    
    private T value;
    
    private CacheLevel level;

    public SimpleValueWrapper(T value,CacheLevel level)
    {
        this.value=value;
        this.level=level;
    }
    
    @Override
    public T getValue() {
        return value;
    }

    @Override
    public CacheLevel getCacheLevel() {
        return level;
    }
    
    
    @Override
    public String toString() {
        return "SimpleValueWrapper [value=" + value + ", level=" + level + "]";
    }
}
