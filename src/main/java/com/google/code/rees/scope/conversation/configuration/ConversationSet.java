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
 * $Id: ConversationSet.java Apr 22, 2012 2:42:58 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.conversation.configuration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.code.rees.scope.util.RequestContextUtil;

/**
 * A set (not a Java set, but backed by one) of all valid conversation names.  Used by the {@link RequestContextUtil} to build a valid
 * conversation request context and prevent the potential for memory leaks presented by the introduction
 * of invalid conversation names into a session.
 * 
 * This class is a lazily-initialized singleton using the singleton-holder pattern developed by Bill Pugh.
 * 
 * @author rees.byars
 */
public class ConversationSet implements Serializable {

	private static final long serialVersionUID = 7638052367886487999L;
	
	private Set<String> backingSet = new HashSet<String>();
	
	private ConversationSet(){}
	
	private static class ConversationSetHolder {
		public static final ConversationSet CONVERSATION_SET = new ConversationSet();
	}
	
	/**
	 * Adds a conversation name to this set
	 * @param conversationName
	 */
	protected void add(String conversationName) {
		this.backingSet.add(conversationName);
	}
	
	/**
	 * 
	 * @param proposedConversationName
	 * @return true if the proposed name is a valid name
	 */
	public boolean contains(String proposedConversationName) {
		return this.backingSet.contains(proposedConversationName);
	}
	
	/**
	 * @return the singleton ConversationSet instance
	 */
	public static ConversationSet get() {
		return ConversationSetHolder.CONVERSATION_SET;
	}

}
