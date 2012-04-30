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
 * $Id: CompositeConversationContext.java Apr 29, 2012 3:59:17 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.conversation.context;

import java.util.Map.Entry;

import com.google.code.rees.scope.conversation.ConversationAdapter;

/**
 * @author rees.byars
 *
 */
public class CompositeConversationContext extends DefaultConversationContext {

	private static final long serialVersionUID = -7936271674093461650L;
	
	public static final String COMPOSITE_CONVERSATION_CONTEXT_NAME = "composite_conversation_context";
	public static final String COMPOSITE_CONVERSATION_CONTEXT_ID = "composite_conversation_context";
	public static final long COMPOSITE_CONVERSATION_CONTEXT_DURATION = 0;

	/**
	 * @param conversationName
	 * @param id
	 * @param duration
	 */
	protected CompositeConversationContext() {
		super(COMPOSITE_CONVERSATION_CONTEXT_NAME, COMPOSITE_CONVERSATION_CONTEXT_ID, COMPOSITE_CONVERSATION_CONTEXT_DURATION);
	}
	
	public static ConversationContext create(ConversationAdapter adapter) {
		ConversationContext context = new CompositeConversationContext();
		for (Entry<String, String> conversationRequestEntry : adapter.getRequestContext().entrySet()) {
			context.putAll(adapter.getConversationContext(conversationRequestEntry.getKey(), conversationRequestEntry.getValue()));
		}
		return context;
	}

}
