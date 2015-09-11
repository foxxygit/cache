package com.foxxy.git.monitor;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultThreadPoolMonitorService implements IThreadPoolMonitorService {

	private final Logger log = LoggerFactory.getLogger(DefaultThreadPoolMonitorService.class);    
	private ThreadPoolExecutor executor;
    private String poolName;
    private long monitoringPeriod = 2L;

    public void run() {
        try {
            for (;;) {
                // 这里获取的size不是那么精确，可是 这里不需要那么精确
                if (executor.getQueue().size() > 0) {

                    monitorThreadPool();
                }
                Thread.sleep(monitoringPeriod * 1000);
            }
        } catch (Exception e) {
        	log.error(e.getMessage());
        }
    }

    /**
     * Returns the task queue used by this executor. Access to the task queue is intended primarily for debugging and
     * monitoring. This queue may be in active use. Retrieving the task queue does not prevent queued tasks from
     * executing.
     *
     */
    public void monitorThreadPool() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("CurrentPoolNamw : ").append(poolName);
        strBuff.append("CurrentPoolSize : ").append(executor.getPoolSize());
        strBuff.append(" - CorePoolSize : ").append(executor.getCorePoolSize());
        strBuff.append(" - MaximumPoolSize : ").append(executor.getMaximumPoolSize());
        strBuff.append(" - ActiveTaskCount : ").append(executor.getActiveCount());
        strBuff.append(" - CompletedTaskCount : ").append(executor.getCompletedTaskCount());
        strBuff.append(" - TotalTaskCount : ").append(executor.getTaskCount());
        strBuff.append(" - isTerminated : ").append(executor.isTerminated());
        strBuff.append("  - queueSize :  ").append(executor.getQueue().size());
        log.info(strBuff.toString());
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public long getMonitoringPeriod() {
        return monitoringPeriod;
    }

    public void setMonitoringPeriod(long monitoringPeriod) {
        this.monitoringPeriod = monitoringPeriod;
    }

    @Override
    public void setExecutorPoolName(String poolName) {
        this.poolName = poolName;
    }

}
