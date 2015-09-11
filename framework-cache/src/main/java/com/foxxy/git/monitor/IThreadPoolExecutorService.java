package com.foxxy.git.monitor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface IThreadPoolExecutorService {

    ThreadPoolExecutor createNewThreadPool(String poolName);

    int getCorePoolSize();

    void setCorePoolSize(int corePoolSize);

    int getMaxPoolSize();

    void setMaxPoolSize(int maximumPoolSize);

    long getKeepAliveTime();

    void setKeepAliveTime(long keepAliveTime);

    int getQueueCapacity();

    void setQueueCapacity(int queueCapacity);

    DefaultRejectedExecutionHandler getDefaultRejectedExecutionHandler();

    void setDefaultRejectedExecutionHandler(DefaultRejectedExecutionHandler defaultRejectedExecutionHandler);
}