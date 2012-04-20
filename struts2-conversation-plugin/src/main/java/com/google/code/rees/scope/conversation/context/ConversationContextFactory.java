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
 *  $Id: ConversationContextFactory.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.context;

import java.io.Serializable;

/**
 * Creates instances of {@link ConversationContext ConversationContexts}
 * 
 * @author rees.byars
 * 
 */
public interface ConversationContextFactory extends Serializable {

	/**
	 * 8 hours
	 */
	public static final long DEFAULT_CONVERSATION_DURATION = 28800000;

	/**
	 * Set the max-idle maxIdleTime for all conversations created by this factory
	 * 
	 * @param maxIdleTime
	 */
	public void setConversationDuration(long duration);

	/**
	 * Create a new context with the given name and id
	 * 
	 * @param conversationName
	 * @param conversationId
	 * @return
	 */
	public ConversationContext create(String conversationName,
			String conversationId);

	/**
	 * Create a new context with the given name, id, and max idle time
	 * 
	 * @param conversationName
	 * @param conversationId
	 * @param maxIdleTime
	 * @return
	 */
	public ConversationContext create(String conversationName,
			String conversationId, long maxIdleTime);
}
