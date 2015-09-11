package com.foxxy.git.event;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foxxy.git.monitor.DefaultThreadPoolExecutorService;
import com.foxxy.git.monitor.DefaultThreadPoolMonitorService;

@Service
public class MessageListenerExcutor {

	private static final int DEFAULT_QUEUECAPACITY = 20000;

	private final Logger log = LoggerFactory
			.getLogger(DefaultThreadPoolMonitorService.class);

	@Autowired
	private SimpleMessageListenerMulticaster muticaster;

	/**
	 * 默认10个线程
	 */
	private ThreadPoolExecutor taskExecutor = new DefaultThreadPoolExecutorService(
			5, 10, 2000, DEFAULT_QUEUECAPACITY, true,
			new DefaultThreadPoolMonitorService())
			.createNewThreadPool("eventDispatch");

	public void setTaskExecutor(ThreadPoolExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void excutor(final MessageEvent event) {

		List<MessageListener> messageListeners = muticaster
				.getMessageListeners(event.getEventName());
		if (CollectionUtils.isEmpty(messageListeners)) {
			log.info("the eventName no avalibe listener {} :",
					new Object[] { event.getEventName() });
			return;
		} else {
			log.info("the eventName avalibe listener {} :",
					new Object[] { messageListeners });
		}

		for (final MessageListener messageListener : messageListeners) {
			if (messageListener.isAsyn()) {
				taskExecutor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							messageListener.onfire(event);
						} catch (Throwable e) {
							log.error(
									"fire messageListener failed!!!messageListener:{}",
									messageListener, e);
							// donothing
						}
					}
				});
			} else {
				messageListener.onfire(event);
			}
		}
	}

}
