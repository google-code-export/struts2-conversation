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
 * $Id: TaskThread.java Apr 15, 2012 9:15:21 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.testutil.thread;


/**
 * An extension of the {@link EasyThread} interface that has methods for adding and removing {@link ThreadTask ThreadTasks}
 * 
 * @author rees.byars
 */
public interface TaskThread extends EasyThread {

	/**
	 * Adds a {@link ThreadTask} to this thread.
	 * @param task
	 */
	public void addTask(ThreadTask task);

	/**
	 * Removes a {@link ThreadTask} from this thread.
	 * @param task
	 */
	public void removeTask(ThreadTask task);

}
