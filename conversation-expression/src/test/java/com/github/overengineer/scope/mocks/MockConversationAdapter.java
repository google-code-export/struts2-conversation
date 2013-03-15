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
 * $Id: MockConversationAdapter.java Apr 23, 2012 3:46:53 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.github.overengineer.scope.mocks;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.context.ConversationContext;
import com.github.overengineer.scope.conversation.context.ConversationContextManager;
import com.github.overengineer.scope.conversation.context.JeeConversationContextManagerProvider;
import com.github.overengineer.scope.util.RequestContextUtil;

/**
 * @author rees.byars
 *
 */
public class MockConversationAdapter extends ConversationAdapter {

	private static final long serialVersionUID = 1L;
	
	Object action = null;
	String actionId = null;
	Map<String, Object> actionContext = new HashMap<String, Object>();
	Map<String, String> requestContext;
	ConversationContextManager contextManager;
	
	public MockConversationAdapter(HttpServletRequest request, JeeConversationContextManagerProvider contextManagerProvider) {
		this.requestContext = RequestContextUtil.getRequestContext(request);
		this.contextManager = contextManagerProvider.getManager(request);
	}
	
	public void setAction(Object action) {
		this.action = action;
	}

	@Override
	public Object getAction() {
		return this.action;
	}
	
	public void setActionContext(Map<String, Object> actionContext) {
		this.actionContext = actionContext;
	}
	
	@Override
	public Map<String, Object> getActionContext() {
		return this.actionContext;
	}
	
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	@Override
	public String getActionId() {
		return this.actionId;
	}

	@Override
	public Map<String, String> getRequestContext() {
		return this.requestContext;
	}
	
	@Override
	public ConversationContext beginConversation(String conversationName, long maxIdleTimeMillis, int maxInstances) {
		return this.contextManager.createContext(conversationName, maxIdleTimeMillis, maxInstances);
	}
	
	@Override
	public ConversationContext getConversationContext(String conversationName, String conversationId) {
		return this.contextManager.getContext(conversationName, conversationId);
	}

	@Override
	public ConversationContext endConversation(String conversationName, String conversationId) {
		return this.contextManager.remove(conversationName, conversationId);
	}
	
	public static MockConversationAdapter init(HttpServletRequest request, JeeConversationContextManagerProvider contextManagerProvider) {
		return new MockConversationAdapter(request, contextManagerProvider);
	}

}
