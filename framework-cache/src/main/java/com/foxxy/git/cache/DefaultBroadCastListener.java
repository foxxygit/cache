package com.foxxy.git.cache;

import com.foxxy.git.spring.CacheManagerHolder;

/**
 * 默认的缓存广播监听实例操作，需要与awareClassName配合使用<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DefaultBroadCastListener implements CacheBroadcastListener {

    @Override
    public boolean notifyElementUpdate(ElementWrapper wrapper) {
        if (ActionEventKey.UPDATE.equals(wrapper.getActionEventKey())) {
            // 时间为空或为0表示不失效
            if (null == wrapper.getLiveSeconds() || 0 == wrapper.getLiveSeconds()) {
                CacheManagerHolder.getInstance().get(wrapper.getManagerName())
                        .getCache(CacheLevel.FIRST, wrapper.getCacheName()).set(wrapper.getKey(), wrapper.getValue());
            } else {
                CacheManagerHolder.getInstance().get(wrapper.getManagerName())
                        .getCache(CacheLevel.FIRST, wrapper.getCacheName())
                        .setonExpire(wrapper.getKey(), wrapper.getValue(), wrapper.getLiveSeconds());
            }
        } else if (ActionEventKey.DELETE.equals(wrapper.getActionEventKey())) {
            CacheManagerHolder.getInstance().get(wrapper.getManagerName())
                    .getCache(CacheLevel.FIRST, wrapper.getCacheName()).evict(wrapper.getKey());
        }
        return true;
    }

    @Override
    public Class<?> getAwareSerialClass() {
        return null;
    }

}
