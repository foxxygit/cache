package com.foxxy.git.cache;

/**
 * 将缓存的改变广播到其他机器上，保持内存级别的缓存同步<br> 
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface CacheBroadcastListener {
    
    boolean notifyElementUpdate(ElementWrapper wrapper);
    
    Class<?> getAwareSerialClass();
}
