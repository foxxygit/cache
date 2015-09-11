package com.foxxy.git.monitor;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {

    private final Logger log = LoggerFactory.getLogger(DefaultRejectedExecutionHandler.class);
    
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

    	log.warn("The pool reject request:{}", new Object[] { executor.toString() });
        throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString());
    }

}
