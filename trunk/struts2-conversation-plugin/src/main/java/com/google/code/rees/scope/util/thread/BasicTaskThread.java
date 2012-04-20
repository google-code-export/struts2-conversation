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
 * $Id: BasicTaskThread.java Apr 15, 2012 9:09:45 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.thread;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author rees.byars
 */
public class BasicTaskThread extends AbstractEasyThread implements
		EasyTaskThread {

	protected Set<ThreadTask> tasks;

	protected BasicTaskThread() {
		tasks = new CopyOnWriteArraySet<ThreadTask>();
	}

	@Override
	public void addTask(ThreadTask task) {
		synchronized (this.tasks) {
			this.tasks.add(task);
		}
	}

	@Override
	public void removeTask(ThreadTask task) {
		synchronized (this.tasks) {
			this.tasks.remove(task);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		synchronized (this.tasks) {
			this.tasks.clear();
		}
	}

	@Override
	protected void doWhileRunning() {
		Iterator<ThreadTask> taskIterator = this.tasks.iterator();
		Set<ThreadTask> deadTasks = new HashSet<ThreadTask>();
		while (taskIterator.hasNext()) {
			ThreadTask task = taskIterator.next();
			if (task.isActive()) {
				task.doTask();
			} else {
				deadTasks.add(task);
			}
		}
		if (deadTasks.size() > 0) {
			this.tasks.removeAll(deadTasks);
		}
	}

	public static BasicTaskThread spawnInstance() {
		BasicTaskThread basicTaskThread = new BasicTaskThread();
		basicTaskThread.start();
		return basicTaskThread;
	}

}
