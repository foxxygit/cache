package com.foxxy.git.event;

import java.util.EventListener;

public interface MessageListener extends EventListener {

    /**
     * 触发事件
     * @param event
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void onfire(MessageEvent event);

    /**
     * 
     * 是否异步执行
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    boolean isAsyn();

}
