package com.foxxy.git.monitor;

import java.util.concurrent.ThreadPoolExecutor;

public interface IThreadPoolMonitorService extends Runnable{

    void monitorThreadPool();

    ThreadPoolExecutor getExecutor();

    void setExecutor(ThreadPoolExecutor executor);
    
    void setExecutorPoolName(String poolName);
}
