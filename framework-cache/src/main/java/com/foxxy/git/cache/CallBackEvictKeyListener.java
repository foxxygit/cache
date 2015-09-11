package com.foxxy.git.cache;

/**
 * key被移除之后的回调函数<br> 
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface CallBackEvictKeyListener<K,V> {
    /**
     * 
     * 功能描述: 触发回调函数<br>
     * 〈功能详细描述〉
     *
     * @param key
     * @param value
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void onCallBack(K key, V value);
}
