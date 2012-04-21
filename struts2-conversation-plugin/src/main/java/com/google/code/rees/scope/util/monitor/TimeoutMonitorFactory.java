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
 * $Id: TimeoutMonitorFactory.java Apr 16, 2012 2:54:09 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.monitor;

/**
 * A factory interface for producing {@link TimeoutMonitor TimeoutMonitors}
 * 
 * @author rees.byars
 */
public interface TimeoutMonitorFactory<T extends Timeoutable<T>> {

	/**
	 * Sets the monitoring frequency of the monitors produced by this factory
	 * @param frequencyMillis
	 */
	public void setMonitoringFrequency(long frequencyMillis);

	/**
	 * returns a TimeoutMonitor with its thread running (already initialized)
	 */
	public TimeoutMonitor<T> spawnMonitor();

}
