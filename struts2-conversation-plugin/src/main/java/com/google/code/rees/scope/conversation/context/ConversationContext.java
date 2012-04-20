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
 *  $Id: ConversationContext.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.context;

import com.google.code.rees.scope.util.monitor.MonitoredContext;

/**
 * The context in which the conversation-scoped beans are placed. There is a
 * separate context for each instance of each conversation.
 * 
 * @author rees.byars
 * 
 */
public abstract interface ConversationContext extends
		MonitoredContext<String, Object, ConversationContext> {

	/**
	 * Get the context's id
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * The name of the conversation for which this context represents an
	 * instance
	 * 
	 * @return
	 */
	public String getConversationName();

}
