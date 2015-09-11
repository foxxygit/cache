package com.foxxy.git.cache;

import java.util.LinkedHashMap;

/**
 * 
 * 简单的LRU实现，不是线程安全，经典的Map cpu100%<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = -3700466745992492679L;

    private int coreSize;
    
    private CallBackEvictKeyListener<K, V> listener;

    public LRUMap(int coreSize,CallBackEvictKeyListener<K, V> listener) {
        super(coreSize + 1, 1.1f, true);
        this.coreSize = coreSize;
        this.listener=listener;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        if(size() > coreSize)
        {   
            listener.onCallBack(eldest.getKey(), eldest.getValue());
            return true;
        }
        return false;
    }
}
