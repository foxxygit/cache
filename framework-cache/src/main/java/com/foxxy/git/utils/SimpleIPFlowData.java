package com.foxxy.git.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的ip流控实现，可以用令牌的模式实现高级流控<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SimpleIPFlowData {

    private AtomicInteger[] data;

    private int slotCount;

    private Timer timer = new Timer();

    class DefaultIPFlowDataManagerTask extends TimerTask {
        @Override
        public void run() {
            rotateSlot();
        }
    }

    public SimpleIPFlowData(int slotCount, int interval) {
        this.slotCount = slotCount;
        data = new AtomicInteger[slotCount];
        for (int i = 0; i < data.length; i++) {
            data[i] = new AtomicInteger(0);
        }
        timer.schedule(new DefaultIPFlowDataManagerTask(), interval, interval);
    }

    public int incrementAndGet(String ip) {
        int index = getIndex(ip);
        return data[index].incrementAndGet();
    }

    public void rotateSlot() {
        for (int i = 0; i < slotCount; i++) {
            data[i].set(0);
        }
    }

    public int getCurrentCount(String ip) {
        int index = getIndex(ip);
        return data[index].get();
    }

    private int getIndex(String ip) {
        int index = 0;
        if (ip != null) {
            index = ip.hashCode() % slotCount;
        }
        if (index < 0)
            index = -index;
        return index;
    }
}
