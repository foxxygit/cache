package com.foxxy.git.cache;

import com.foxxy.git.cache.CacheProxy.ValueWrapper;

/**
 * 空对象模式，避免上层丑陋的判空操作<br> 
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class NullValueWrapper<T> implements ValueWrapper<T> {

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public CacheLevel getCacheLevel() {
        return null;
    }

}
