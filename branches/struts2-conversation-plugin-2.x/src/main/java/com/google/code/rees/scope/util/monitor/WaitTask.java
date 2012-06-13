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
 * $Id: WaitTask.java Apr 17, 2012 3:12:04 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.monitor;

import java.io.Serializable;

import com.google.code.rees.scope.util.thread.ThreadTask;

/**
 * @author rees.byars
 * 
 */
public class WaitTask implements ThreadTask, Serializable {

	private static final long serialVersionUID = 3539233042598738551L;

	private long wait;

	protected WaitTask(long wait) {
		this.wait = wait;
	}

	@Override
	public void doTask() {
		synchronized (this) {
			try {
				this.wait(this.wait);
			} catch (InterruptedException e) {
				// no worries
			}
		}
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void cancel() {
		// never!!!
	}

	public void setWait(long waitMillis) {
		this.wait = waitMillis;
	}

	public static WaitTask create(long wait) {
		return new WaitTask(wait);
	}

}
