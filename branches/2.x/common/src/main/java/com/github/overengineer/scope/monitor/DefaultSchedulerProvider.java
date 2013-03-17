package com.github.overengineer.scope.monitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.overengineer.scope.CommonConstants.Properties;
import com.github.overengineer.scope.container.PostConstructable;
import com.github.overengineer.scope.container.Property;

public class DefaultSchedulerProvider implements SchedulerProvider, PostConstructable {
	
	private static final long serialVersionUID = -6832427883773358141L;
	
	protected int monitoringThreadPoolSize;
	protected transient ScheduledExecutorService scheduler;
    protected Lock schedulerGuard = new ReentrantLock();
    
    @Property(Properties.MONITORING_THREAD_POOL_SIZE)
    public void setMonitoringThreadPoolSize(int monitoringThreadPoolSize) {
    	this.monitoringThreadPoolSize = monitoringThreadPoolSize;
    }

	@Override
	public void init() {
		synchronized (SchedulerShutdownListener.class) {
    		scheduler = SchedulerShutdownListener.getScheduler();
    		if (scheduler == null) {
    			scheduler = Executors.newScheduledThreadPool(monitoringThreadPoolSize, new ThreadFactory() {
		    		
		    		AtomicInteger id = new AtomicInteger(0);

					@Override
					public Thread newThread(Runnable runnable) {
						Thread thread = new Thread(runnable);
						thread.setDaemon(true);
						thread.setName("ConversationTimeoutMonitoringThread-" + id.getAndIncrement());
						return thread;
					}
		    		
		    	});
    			SchedulerShutdownListener.setScheduler(scheduler);
    		}
    	}
	}

	@Override
	public ScheduledExecutorService getScheduler() {
		if (scheduler != null) {
			return scheduler;
		}
		schedulerGuard.lock();
		try {
			if (scheduler !=null) {
				return scheduler;
			}
			init();
			return scheduler;
		} finally {
			schedulerGuard.unlock();
		}
	}

}
