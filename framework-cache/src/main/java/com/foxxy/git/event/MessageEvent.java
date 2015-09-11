package com.foxxy.git.event;

import java.util.EventObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageEvent extends EventObject{

    private static final long serialVersionUID = 5150231587161672038L;
    
    private String eventName;
    
    private Map<String,Object> param=new ConcurrentHashMap<String, Object>(16);
    
    public Map<String, Object> getParam() {
        return param;
    }

    public MessageEvent(Object source) 
    {
        super(source);
    }
    
    public MessageEvent(String eventName,Object source) 
    {   
        super(source);
        this.eventName=eventName;
    } 
    
    public MessageEvent(String eventName,Object ...source) 
    {   
        super(source);
        this.eventName=eventName;
    } 
    
    public String getEventName()
    {
        return eventName;
    }

    @Override
    public String toString() {
        return "MessageEvent [eventName=" + eventName + ", param=" + param + "]";
    }
    
}
