package com.foxxy.git.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 带最大条目限制的缓存包装<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ConcurrentLimitedHashMap<K, V> extends ConcurrentHashMap<K, V> {

    private static final long serialVersionUID = -1395684447498641466L;

    private int size;

    public ConcurrentLimitedHashMap(int size) {
        this.size = size;
    }

    @Override
	public V put(K key, V value) {
        //
        if (!(this.size() > size)) {
            return super.put(key, value);
        }
        return value;
    }

    @Override
	public V putIfAbsent(K key, V value) {
        //
        if (!(this.size() > size)) {
            return super.putIfAbsent(key, value);
        }
        return value;
    }

}
