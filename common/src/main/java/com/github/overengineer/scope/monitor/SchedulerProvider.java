package com.github.overengineer.scope.monitor;

import java.io.Serializable;
import java.util.concurrent.ScheduledExecutorService;

import com.github.overengineer.scope.container.PostConstructable;

public interface SchedulerProvider extends Serializable, PostConstructable {
	
	ScheduledExecutorService getScheduler();

}
