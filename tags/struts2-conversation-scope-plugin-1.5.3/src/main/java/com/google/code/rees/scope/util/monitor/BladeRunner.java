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
 * $Id: BladeRunner.java Apr 25, 2012 1:52:33 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.monitor;

import com.google.code.rees.scope.util.thread.ThreadTask;

/**
 * A basic implementation of {@link TimeoutRunner} inspired by Harrison Ford.
 * 
 * @author rees.byars
 */
public class BladeRunner<T extends Timeoutable<T>> implements TimeoutRunner<T> {

	private static final long serialVersionUID = -2296155896962664978L;
	private ThreadTask threadTask;
	private T timeoutable;
	
	protected BladeRunner(){}
	
	/**
	 * creates and returns a BladeRunner instance
	 * 
	 * @param <TT>
	 * @param timeoutable
	 * @return
	 */
	public static <TT extends Timeoutable<TT>> BladeRunner<TT> create(TT timeoutable) {
		BladeRunner<TT> runner = new BladeRunner<TT>();
		runner.setTimeoutable(timeoutable);
		runner.setThreadTask(TimeoutTask.create(timeoutable));
		return runner;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		this.threadTask.doTask();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getTimeoutable() {
		return this.timeoutable;
	}
	
	protected void setTimeoutable(T timeoutable) {
		this.timeoutable = timeoutable;
	}
	
	protected ThreadTask getThreadTask() {
		return this.threadTask;
	}
	
	protected void setThreadTask(ThreadTask threadTask) {
		this.threadTask = threadTask;
	}
	
}
