/***********************************************************************************************************************
 *
 * Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 * =================================================================================================================
 *
 * Copyright (C) 2012 by Rees Byars
 * http://code.google.com/p/struts2-conversation/
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * $Id: ScheduledExecutorTimeoutMonitor.java Apr 24, 2012 9:24:23 AM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of the {@link TimeoutMonitor} that makes use of a {@link ScheduledExecutorService}.
 * 
 * Cache's tasks and will add them to a new ScheduledExecutorService after serialization in order
 * to continue monitoring in cases such as cluster replication.
 * 
 * Of note:  this class depends on being given a new scheduler after serialization, is does not
 * create its own schedulers.  Likewise, it must be given an instance when it is first created as well.
 * 
 * @author rees.byars
 */
public class ScheduledExecutorTimeoutMonitor<T extends Timeoutable<T>> implements TimeoutMonitor<T>, TimeoutListener<T> {

	private static final long serialVersionUID = -1502605748762224777L;
	
	/**
	 * the delay between adding a Timeoutable to the scheduler and the time of the first check of the Timeoutable's remaining time
	 */
	public static final long MONITORING_DELAY = 1000L;
	
	protected Map<String, TimeoutRunner<T>> timeoutRunners = new HashMap<String, TimeoutRunner<T>>();
	protected transient Map<String, ScheduledFuture<?>> scheduledFutures = null;
	protected transient ScheduledExecutorService scheduler = null;
	protected long monitoringFrequency = DEFAULT_MONITOR_FREQUENCY;
	
	protected ScheduledExecutorTimeoutMonitor(){}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMonitoringFrequency(long frequencyMillis) {
		this.monitoringFrequency = frequencyMillis;
	}
	
	/**
	 * sets the scheduler to be used
	 */
	public void setScheduler(ScheduledExecutorService scheduler) {
		if (this.scheduler == null) {
			this.scheduler = scheduler;
			this.init();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		synchronized(this.timeoutRunners) {
			if (scheduledFutures == null) {
				scheduledFutures = new HashMap<String, ScheduledFuture<?>>();
			}
			for (Entry<String, TimeoutRunner<T>> entry : this.timeoutRunners.entrySet()) {
				String targetId = entry.getKey();
				ScheduledFuture<?> future = this.scheduledFutures.get(targetId);
				if (future == null) {
					future = this.scheduler.scheduleAtFixedRate(entry.getValue(), 1000L, this.monitoringFrequency, TimeUnit.MILLISECONDS);
					this.scheduledFutures.put(targetId, future);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		synchronized(this.timeoutRunners) {
			if (this.scheduledFutures != null) {
				for (ScheduledFuture<?> future : this.scheduledFutures.values()) {
					future.cancel(true);
				}
				this.scheduledFutures.clear();
			}
			this.timeoutRunners.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTimeoutable(T timeoutable) {
		synchronized (this.timeoutRunners) {
			String targetId = timeoutable.getId();
			if (!this.timeoutRunners.containsKey(targetId)) {
				TimeoutRunner<T> timeoutRunner = BladeRunner.create(timeoutable);
				this.timeoutRunners.put(targetId, timeoutRunner);
				ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(timeoutRunner, MONITORING_DELAY, this.monitoringFrequency, TimeUnit.MILLISECONDS);
				this.scheduledFutures.put(targetId, future);
				timeoutable.addTimeoutListener(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTimeoutable(T timeoutable) {
		synchronized(this.timeoutRunners) {
			String targetId = timeoutable.getId();
			ScheduledFuture<?> future = this.scheduledFutures.remove(targetId);
			if (future != null) {
				future.cancel(true);
			}
			this.timeoutRunners.remove(targetId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTimeout(T timeoutable) {
		this.removeTimeoutable(timeoutable);
	}
	
	/**
	 * used to create an instance
	 * 
	 * @param <TT>
	 * @param scheduler
	 * @param monitoringFrequency
	 * @return
	 */
	public static <TT extends Timeoutable<TT>> ScheduledExecutorTimeoutMonitor<TT> spawnInstance(ScheduledExecutorService scheduler, long monitoringFrequency) {
		ScheduledExecutorTimeoutMonitor<TT> monitor = new ScheduledExecutorTimeoutMonitor<TT>();
		monitor.setMonitoringFrequency(monitoringFrequency);
		monitor.setScheduler(scheduler);
		return monitor;
	}

}
