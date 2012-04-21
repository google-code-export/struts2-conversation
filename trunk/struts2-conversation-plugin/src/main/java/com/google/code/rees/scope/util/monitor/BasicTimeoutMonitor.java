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
 * $Id: BasicTimeoutMonitor.java Apr 16, 2012 9:43:40 AM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.monitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.code.rees.scope.util.thread.BasicTaskThread;
import com.google.code.rees.scope.util.thread.TaskThread;

/**
 * @author rees.byars
 * 
 */
public class BasicTimeoutMonitor<T extends Timeoutable<T>> implements
		TimeoutMonitor<T>, TimeoutListener<T> {

	private static final long serialVersionUID = -7924540987291582366L;

	protected Map<String, TimeoutTask> timeoutTasks;
	protected transient TaskThread taskThread;
	protected WaitTask waiter;
	protected long monitoringFrequency = DEFAULT_MONITOR_FREQUENCY;
	
	protected BasicTimeoutMonitor() {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		this.waiter = WaitTask.create(this.monitoringFrequency);
		this.taskThread = BasicTaskThread.spawnInstance();
		this.taskThread.addTask(this.waiter);
		if (this.timeoutTasks == null) {
			timeoutTasks = new HashMap<String, TimeoutTask>();
		}
		synchronized (this.timeoutTasks) {
			for (TimeoutTask task : this.timeoutTasks.values()) {
				this.taskThread.addTask(task);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		if (this.taskThread != null) {
			this.taskThread.destroy();
		}
		if (this.timeoutTasks != null) {
			this.timeoutTasks.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMonitoringFrequency(long frequencyMillis) {
		this.monitoringFrequency = frequencyMillis;
		if (this.waiter != null) {
			this.waiter.setWait(this.monitoringFrequency);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTimeoutable(T timeoutable) {
		if (!this.timeoutTasks.containsKey(timeoutable.getId())) {
			timeoutable.addTimeoutListener(this);
			TimeoutTask task = TimeoutTask.create(timeoutable);
			synchronized (this.timeoutTasks) {
				this.timeoutTasks.put(timeoutable.getId(), task);
			}
			this.taskThread.addTask(task);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTimeoutable(T timeoutable) {
		this.taskThread.removeTask(this.timeoutTasks.remove(timeoutable.getId()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTimeout(T timeoutable) {
		this.timeoutTasks.remove(timeoutable.getId());
	}
	
	public static <TT extends Timeoutable<TT>> BasicTimeoutMonitor<TT> spawnInstance() {
		BasicTimeoutMonitor<TT> monitor = new BasicTimeoutMonitor<TT>();
		monitor.init();
		return monitor;
	}
	
	public static <TT extends Timeoutable<TT>> BasicTimeoutMonitor<TT> spawnInstance(long frequencyMillis) {
		BasicTimeoutMonitor<TT> monitor = new BasicTimeoutMonitor<TT>();
		monitor.setMonitoringFrequency(frequencyMillis);
		monitor.init();
		return monitor;
	}

	/**
	 * An override of the writeObject method for serialization.  It just makes sure
	 * that the {@link #taskThread} is destroyed.  Not likely to be necessary in
	 * the real world, but good for a safe measure.
	 * 
	 * @param oos
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream oos) throws IOException {
		this.taskThread.destroy();
		oos.defaultWriteObject();
	}

	/**
	 * An override of the readObject method for serialization.  It
	 * calls {@link #init()} after deserialization in order to startup
	 * a new {@link #taskThread} and assign it the {@link #timeoutTasks}
	 * in order to have the monitor continue monitoring after serialization.
	 * 
	 * @param ois
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		this.init();
	}

}
