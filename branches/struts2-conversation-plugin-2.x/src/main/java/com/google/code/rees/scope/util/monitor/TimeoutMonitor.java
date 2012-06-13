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
 * $Id: TimeoutMonitor.java Apr 16, 2012 9:33:57 AM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.monitor;

import java.io.Serializable;

/**
 * This interface works in conjunction with the {@link Timeoutable} interface to
 * provide a simple timeout mechanism
 * 
 * @author rees.byars
 */
public interface TimeoutMonitor<T extends Timeoutable<T>> extends Serializable {

	/**
	 * 5 minutes
	 */
	public static final long DEFAULT_MONITOR_FREQUENCY = 300000;

	/**
	 * The frequency, in milliseconds, that this monitor will check its
	 * {@link Timeoutable Timeoutables} for their remaining times. It should be
	 * noted that the frequency is guaranteed to not be less the the given time,
	 * but it may incrementally exceed the given time as more
	 * {@link Timeoutable Timeoutables} are added. This is due to the time
	 * required to check each of the Timeoutables.
	 * 
	 * @param frequencyMillis
	 */
	public void setMonitoringFrequency(long frequencyMillis);

	/**
	 * Adds the {@link Timeoutable} to this monitor
	 * 
	 * @param timeoutable
	 */
	public void addTimeoutable(T timeoutable);

	/**
	 * Removes this {@link Timeoutable} from this monitor
	 * 
	 * @param timeoutable
	 */
	public void removeTimeoutable(T timeoutable);

	/**
	 * Initializes this monitor, beginning a background thread for monitoring
	 * its {@link Timeoutable Timeoutables}.
	 */
	public void init();

	/**
	 * Destroys this monitor, stopping its background thread and clearing its
	 * {@link Timeoutable} cache.
	 */
	public void destroy();

}
