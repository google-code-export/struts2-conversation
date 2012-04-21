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
 * $Id: TimeoutTask.java Apr 17, 2012 2:27:43 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.monitor;

import java.io.Serializable;

import com.google.code.rees.scope.util.thread.ThreadTask;

/**
 * @author rees.byars
 * 
 */
public class TimeoutTask implements ThreadTask, Serializable {

	private static final long serialVersionUID = 8002426005125447219L;

	protected Timeoutable<?> timeoutable;
	protected boolean active;

	protected TimeoutTask(Timeoutable<?> timeoutable) {
		this.timeoutable = timeoutable;
		this.active = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean isActive() {
		return this.active;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void cancel() {
		this.active = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doTask() {
		if (this.timeoutable.getRemainingTime() <= 0) {
			this.timeoutable.timeout();
			this.cancel();
		}
	}

	public static TimeoutTask create(Timeoutable<?> timeoutable) {
		return new TimeoutTask(timeoutable);
	}

}
