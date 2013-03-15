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
package com.github.overengineer.scope.monitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.overengineer.scope.CommonConstants.Properties;
import com.github.overengineer.scope.container.Component;
import com.github.overengineer.scope.container.Property;

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
	
	private static final int INITIAL_CAPACITY = 8;
	private static final float LOAD_FACTOR = .9f;
	
	/**
	 * the delay between adding a Timeoutable to the scheduler and the time of the first check of the Timeoutable's remaining time
	 */
	public static final long MONITORING_DELAY = 1000L;
	
	protected Map<String, TimeoutRunner<T>> timeoutRunners = new Hashtable<String, TimeoutRunner<T>>(INITIAL_CAPACITY, LOAD_FACTOR);
	protected transient Map<String, ScheduledFuture<?>> scheduledFutures;
	protected transient ScheduledExecutorService scheduler;
	protected long monitoringFrequency;
	protected SchedulerProvider schedulerProvider;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Property(Properties.MONITORING_FREQUENCY)
	public void setMonitoringFrequency(long frequencyMillis) {
		this.monitoringFrequency = frequencyMillis;
	}
	
	/**
	 * sets the scheduler to be used
	 */
	@Component
	public void setSchedulerProvider(SchedulerProvider schedulerProvider) {
		this.schedulerProvider = schedulerProvider;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		this.scheduler = this.schedulerProvider.getScheduler();
		synchronized(this.timeoutRunners) {
			if (this.scheduledFutures == null) {
				this.scheduledFutures = new Hashtable<String, ScheduledFuture<?>>(INITIAL_CAPACITY, LOAD_FACTOR);
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
			for (ScheduledFuture<?> future : this.scheduledFutures.values()) {
				future.cancel(true);
			}
			this.scheduledFutures.clear();
			this.timeoutRunners.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTimeoutable(final T timeoutable) {
		final String targetId = timeoutable.getId();
		if (!this.timeoutRunners.containsKey(targetId)) {
			@SuppressWarnings("serial")
			TimeoutRunner<T> timeoutRunner = new TimeoutRunner<T>() {
				
				private transient WeakReference<T> timeoutableReference = new WeakReference<T>(timeoutable);
				private T serializableRef = null;

				@Override
				public void run() {
					T t = this.getTimeoutable();
					if (t == null) {
						ScheduledFuture<?> future = scheduledFutures.remove(targetId);
						if (future != null) {
							future.cancel(true);
						}
						timeoutRunners.remove(targetId);
					} else if (t.getRemainingTime() <= 0) {
						t.timeout();
					}
				}

				@Override
				public T getTimeoutable() {
					return this.timeoutableReference.get();
				}
				
				private void writeObject(ObjectOutputStream out) throws IOException {
					this.serializableRef = this.timeoutableReference.get();
					out.defaultWriteObject();
				}
				
				private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
					in.defaultReadObject();
					timeoutableReference = new WeakReference<T>(this.serializableRef);
					this.serializableRef = null;
				}
				
			};
			this.timeoutRunners.put(targetId, timeoutRunner);
			ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(timeoutRunner, MONITORING_DELAY, this.monitoringFrequency, TimeUnit.MILLISECONDS);
			this.scheduledFutures.put(targetId, future);
			timeoutable.addTimeoutListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTimeoutable(T timeoutable) {
		String targetId = timeoutable.getId();
		ScheduledFuture<?> future = this.scheduledFutures.remove(targetId);
		if (future != null) {
			future.cancel(true);
		}
		this.timeoutRunners.remove(targetId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTimeout(T timeoutable) {
		this.removeTimeoutable(timeoutable);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.init();
	}

}
