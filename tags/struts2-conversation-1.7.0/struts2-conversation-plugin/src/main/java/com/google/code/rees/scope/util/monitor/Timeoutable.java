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
 * $Id: Timeoutable.java Apr 16, 2012 9:37:19 AM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.monitor;

import java.io.Serializable;

/**
 * This interface can be used in conjunction with a {@link TimeoutMonitor} to provide a simple timeout mechanism
 * 
 * @author rees.byars
 */
public interface Timeoutable<T extends Timeoutable<T>> extends Serializable {

	/**
	 * Sets the time after which this Timeoutable's {@link #timeout()} method will be called
	 * @param maxIdleTime
	 */
	public void setMaxIdleTime(long maxIdleTime);

	/**
	 * A unique ID used to identify this Timeoutable
	 * @return
	 */
	public String getId();

	/**
	 * The time remaining until this Timeoutable's {@link #timeout()} method will be called
	 * @return
	 */
	public long getRemainingTime();

	/**
	 * Called when this Timeoutable's {@link #getRemainingTime()} is equal to or less than zero (i.e. the max idle time has been exceeded).
	 */
	public void timeout();

	/**
	 * Resets this Timeoutable's remaining time to the max idle time
	 */
	public void reset();

	/**
	 * Add a listener whose {@link TimeoutListener#onTimeout(Object)} method will be called when this Timeoutable's
	 * {@link #timeout()} method is called.  
	 * 
	 * @param timeoutListener
	 */
	public void addTimeoutListener(TimeoutListener<T> timeoutListener);

}
