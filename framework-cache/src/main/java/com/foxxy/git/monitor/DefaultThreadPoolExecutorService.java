package com.foxxy.git.monitor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.foxxy.git.utils.NamedThreadFactory;

/**
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DefaultThreadPoolExecutorService implements IThreadPoolExecutorService {

    private int corePoolSize;
    private int maxPoolSize;
    private long keepAliveTime;
    private int queueCapacity;
    private boolean isMonitor = false;
    private IThreadPoolMonitorService monitorService;

    private DefaultRejectedExecutionHandler rejectedExecutionHandler = new DefaultRejectedExecutionHandler();

    public DefaultThreadPoolExecutorService(int corePoolSize, int maxPoolSize, long keepAliveTime, int queueCapacity,
            boolean isMonitor, IThreadPoolMonitorService monitorService) {
        super();
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.queueCapacity = queueCapacity;
        this.isMonitor = isMonitor;
        this.monitorService = monitorService;
    }

    @Override
    public synchronized ThreadPoolExecutor createNewThreadPool(String poolName) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(getCorePoolSize(), getMaxPoolSize(), getKeepAliveTime(),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(getQueueCapacity()),
                getDefaultRejectedExecutionHandler());
        executor.setThreadFactory(new NamedThreadFactory(poolName));
        if (isMonitor) {
            monitorService.setExecutor(executor);
            monitorService.setExecutorPoolName(poolName);
            Thread monitor = new Thread(monitorService);
            monitor.start();
        }
        return executor;
    }

    @Override
    public int getCorePoolSize() {
        return corePoolSize;
    }

    @Override
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    @Override
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    @Override
    public void setMaxPoolSize(int maximumPoolSize) {
        this.maxPoolSize = maximumPoolSize;
    }

    @Override
    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    @Override
    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    @Override
    public int getQueueCapacity() {
        return queueCapacity;
    }

    @Override
    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    @Override
    public DefaultRejectedExecutionHandler getDefaultRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    @Override
    public void setDefaultRejectedExecutionHandler(DefaultRejectedExecutionHandler defaultRejectedExecutionHandler) {
        this.rejectedExecutionHandler = defaultRejectedExecutionHandler;
    }

    public boolean isMonitor() {
        return isMonitor;
    }

    public void setMonitor(boolean isMonitor) {
        this.isMonitor = isMonitor;
    }

    public void setMonitorService(IThreadPoolMonitorService monitorService) {
        this.monitorService = monitorService;
    }
}
