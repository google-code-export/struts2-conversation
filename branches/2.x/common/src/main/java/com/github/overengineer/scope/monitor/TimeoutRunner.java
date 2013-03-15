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
 * $Id: TimeoutRunner.java Apr 25, 2012 4:26:55 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.github.overengineer.scope.monitor;

import java.io.Serializable;

/**
 * A handy runnable interface whose implementations should have a {@link #run()} method
 * that will be executed periodically to perform monitoring of its {@link Timeoutable}.
 * 
 * 
 * @author rees.byars
 */
public interface TimeoutRunner<T extends Timeoutable<T>> extends Runnable, Serializable {
	
	/**
	 * acquire the Timeoutable that this runner is monitoring
	 * @return
	 */
	public T getTimeoutable();

}
