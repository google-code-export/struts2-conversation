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
 * $Id: Conversations.java Jun 11, 2012 11:25:43 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.conversation;

import com.google.code.rees.scope.conversation.annotations.GuardType;

/**
 * @author rees.byars
 * 
 */
public enum Conversations implements Conversation {

	MAIN("main", 45L, 1, GuardType.NONE);

	private String name;
	private long maxIdleMinutes;
	private int maxInstances;
	private GuardType guardType;

	private Conversations(String name, long maxIdleMinutes, int maxInstances, GuardType guardType) {
		this.name = name + ConversationConstants.CONVERSATION_NAME_SUFFIX;
		this.maxIdleMinutes = maxIdleMinutes;
		this.maxInstances = maxInstances;
		this.guardType = guardType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getMaxIdleMinutes() {
		return this.maxIdleMinutes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxInstances() {
		return this.maxInstances;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GuardType getGuardType() {
		return this.guardType;
	}

}
