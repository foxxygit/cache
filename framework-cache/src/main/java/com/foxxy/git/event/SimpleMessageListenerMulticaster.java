package com.foxxy.git.event;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.foxxy.git.annotation.Subscribe;

/**
 * 监听广播中心
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class SimpleMessageListenerMulticaster implements BeanPostProcessor{
    
    private Map<String,List<MessageListener>> listenerMap=new ConcurrentHashMap<String,List<MessageListener>>(16);
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(MessageListener.class.isAssignableFrom(bean.getClass())&& bean.getClass().isAnnotationPresent(Subscribe.class))
        {   
            Subscribe subscribe=bean.getClass().getAnnotation(Subscribe.class);
            if(listenerMap.containsKey(subscribe.value()))
            {
                listenerMap.get(subscribe.value()).add((MessageListener)bean);
            }
            else
            {
                listenerMap.put(subscribe.value(), Lists.newArrayList((MessageListener)bean));
            }
        }
        return bean;
    }
    
    public List<MessageListener> getMessageListeners(String eventKey)
    {
        return listenerMap.get(eventKey);
    }


}
