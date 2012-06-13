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
 *  $Id: ConversationClassConfigurationImpl.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.processing.ConversationProcessor;

/**
 * This class is used to cache the fields and action IDs for a single class for
 * a single conversation.
 * 
 * @see {@link ConversationConfigurationProvider}
 * @see {@link ConversationProcessor}
 * 
 * @author rees.byars
 * 
 */
public class ConversationClassConfigurationImpl implements ConversationClassConfiguration {

	private Map<String, Field> fields;
	private Set<String> actionIds;
	private Set<String> beginActionIds;
	private Set<String> endActionIds;
	private Set<String> guardedActionIds;
	private Map<String, Long> beginActionIdleTimes;
	private Map<String, Integer> beginActionMaxInstances;
	private String conversationName;

	public ConversationClassConfigurationImpl(String conversationName) {
		this.fields = new HashMap<String, Field>();
		this.actionIds = new HashSet<String>();
		this.beginActionIds = new HashSet<String>();
		this.endActionIds = new HashSet<String>();
		this.guardedActionIds = new HashSet<String>();
		this.beginActionIdleTimes = new HashMap<String, Long>();
		this.beginActionMaxInstances = new HashMap<String, Integer>();
		this.conversationName = ConversationUtil.sanitizeName(conversationName) + ConversationConstants.CONVERSATION_NAME_SUFFIX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#addField(java.lang.String,
	 * java.lang.reflect.Field)
	 */
	@Override
	public void addField(String name, Field field) {
		this.fields.put(name, field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#getFields()
	 */
	@Override
	public Map<String, Field> getFields() {
		return this.fields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#addAction(java.lang.String, boolean)
	 */
	@Override
	public void addAction(String actionId, boolean isGuarded) {
		this.actionIds.add(actionId);
		if (isGuarded) {
			this.guardedActionIds.add(actionId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#addBeginAction(java.lang.String,
	 * java.lang.Long, java.lang.Integer)
	 */
	@Override
	public void addBeginAction(String actionId, Long maxIdleTimeMillis, Integer maxInstances) {
		this.actionIds.add(actionId);
		this.beginActionIds.add(actionId);
		this.beginActionIdleTimes.put(actionId, maxIdleTimeMillis);
		this.beginActionMaxInstances.put(actionId, maxInstances);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#addEndAction(java.lang.String)
	 */
	@Override
	public void addEndAction(String actionId) {
		this.actionIds.add(actionId);
		this.endActionIds.add(actionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#containsAction(java.lang.String)
	 */
	@Override
	public boolean containsAction(String actionId) {
		return this.actionIds.contains(actionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#isBeginAction(java.lang.String)
	 */
	@Override
	public boolean isBeginAction(String actionId) {
		return this.beginActionIds.contains(actionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#isEndAction(java.lang.String)
	 */
	@Override
	public boolean isEndAction(String actionId) {
		return this.endActionIds.contains(actionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#isGuarded(java.lang.String)
	 */
	@Override
	public boolean isGuarded(String actionId) {
		return this.guardedActionIds.contains(actionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#getMaxIdleTime(java.lang.String)
	 */
	@Override
	public long getMaxIdleTime(String beginActionId) {
		return this.beginActionIdleTimes.get(beginActionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#getMaxInstances(java.lang.String)
	 */
	@Override
	public int getMaxInstances(String beginActionId) {
		return this.beginActionMaxInstances.get(beginActionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.rees.scope.conversation.configuration.
	 * ConversationClassConfiguration#getConversationName()
	 */
	@Override
	public String getConversationName() {
		return this.conversationName;
	}
}
