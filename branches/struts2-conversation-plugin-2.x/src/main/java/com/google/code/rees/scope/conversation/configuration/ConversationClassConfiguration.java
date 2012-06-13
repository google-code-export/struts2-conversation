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
 * $Id: ConversationClassConfiguration.java Jun 8, 2012 2:25:26 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.conversation.configuration;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.code.rees.scope.conversation.ConversationAdapter;

/**
 * @author 20833
 * 
 */
public interface ConversationClassConfiguration {

	/**
	 * Add a field to the configuration
	 * 
	 * @param name
	 * @param field
	 */
	public abstract void addField(String name, Field field);

	/**
	 * Get the cached fields for conversation
	 * 
	 * @return
	 */
	public abstract Map<String, Field> getFields();

	/**
	 * Add an actionId for an intermediate action
	 * 
	 * @see {@link ConversationAdapter#getActionId()}
	 * @see {@link ConversationArbitrator#getName(Method)}
	 * @param actionId
	 */
	public abstract void addAction(String actionId, boolean isGuarded);

	/**
	 * Add an actionId for a begin action
	 * 
	 * @see {@link ConversationAdapter#getActionId()}
	 * @see {@link ConversationArbitrator#getName(Method)}
	 * @param actionId
	 */
	public abstract void addBeginAction(String actionId, Long maxIdleTimeMillis, Integer maxInstances);

	/**
	 * Add an actionId for an end action
	 * 
	 * @see {@link ConversationAdapter#getActionId()}
	 * @see {@link ConversationArbitrator#getName(Method)}
	 * @param actionId
	 */
	public abstract void addEndAction(String actionId);

	/**
	 * Indicates whether the actionId identifies the action as an intermediate
	 * member for this conversation
	 * 
	 * @see {@link ConversationAdapter#getActionId()}
	 * @see {@link ConversationArbitrator#getName(Method)}
	 * @param actionId
	 * @return
	 */
	public abstract boolean containsAction(String actionId);

	/**
	 * Indicates whether the actionId identifies the action as a begin member
	 * for this conversation
	 * 
	 * @see {@link ConversationAdapter#getActionId()}
	 * @see {@link ConversationArbitrator#getName(Method)}
	 * @param actionId
	 * @return
	 */
	public abstract boolean isBeginAction(String actionId);

	/**
	 * Indicates whether the actionId identifies the action as an end member for
	 * this conversation
	 * 
	 * @see {@link ConversationAdapter#getActionId()}
	 * @see {@link ConversationArbitrator#getName(Method)}
	 * @param actionId
	 * @return
	 */
	public abstract boolean isEndAction(String actionId);

	/**
	 * 
	 * @param actionId
	 * @return true if the action is Guarded
	 * 
	 * @see com.google.code.rees.scope.conversation.annotations.GuardType
	 *      GuardType
	 */
	public abstract boolean isGuarded(String actionId);

	/**
	 * given the begin action's ID, returns the max idle time (in milliseconds)
	 * for the conversation created by that action
	 * 
	 * @param beginActionId
	 * @return
	 */
	public abstract long getMaxIdleTime(String beginActionId);

	/**
	 * given the begin action's ID, returns the max instances allowed for the
	 * conversation created by that action
	 * 
	 * @param beginActionId
	 * @return
	 */
	public abstract int getMaxInstances(String beginActionId);

	/**
	 * Returns the name of the conversation that this configuration is for
	 * 
	 * @return
	 */
	public abstract String getConversationName();

}