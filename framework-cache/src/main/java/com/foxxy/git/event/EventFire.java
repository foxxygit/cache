package com.foxxy.git.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventFire {

    @Autowired
    private MessageListenerExcutor excutor;

    public void fire(MessageEvent event) {

        excutor.excutor(event);
    }
}
