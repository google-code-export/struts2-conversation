package com.github.overengineer.scope.monitor;

import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SchedulerShutdownListener implements ServletContextListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(SchedulerShutdownListener.class);
	
	private static ScheduledExecutorService scheduler;
	
	public static void setScheduler(ScheduledExecutorService scheduler) {
		SchedulerShutdownListener.scheduler = scheduler;
	}
	
	public static ScheduledExecutorService getScheduler() {
		return SchedulerShutdownListener.scheduler;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {	
		LOG.info("Initializing SchedulerShutdownListener");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		SchedulerShutdownListener.scheduler.shutdownNow();
		LOG.info("Scheduler has been shutdown");
	}

}
