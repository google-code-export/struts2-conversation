/*******************************************************************************
 * 
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 * 
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 * 
 * **********************************************************************************************************************
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 * 
 * **********************************************************************************************************************
 * 
 *  $Id: EasyThread.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.testutil.thread;

/**
 * 
 * This interface provides a simple mechanism for interacting with {@link Thread Threads}.
 * 
 * @author rees.byars
 */
public interface EasyThread {

	/**
	 * Begins this thread.  If an underlying {@link Thread} does not already exist, it is created and started.  If it does already
	 * exist, its state is returned to running.
	 */
	public void start();

	/**
	 * Stops this thread.  The underlying {@link Thread} will still exist, but in effect it will cease to execute until it is started again.
	 */
	public void stop();

	/**
	 * Safely stops and destroys the underlying thread.  For the purposes of safety, the underlying thread cannot be guaranteed
	 * to completely, immediately cease, but it will be stopped as soon as possible in a safe manner.
	 */
	public void destroy();

}
