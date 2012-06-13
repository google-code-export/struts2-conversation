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
 *  $Id: SpringConversationAdapter.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.util.RequestContextUtil;
import com.google.code.rees.scope.util.SessionContextUtil;

/**
 * An implementation of the {@link ConversationAdapter} for use with Spring MVC.
 * 
 * @author rees.byars
 * 
 */
public class SpringConversationAdapter extends ConversationAdapter {

	private static final long serialVersionUID = 5664922664767226366L;

	protected ConversationContextManager conversationContextManager;
	protected Map<String, Object> sessionContext;
	protected Map<String, String> requestContext;
	protected Object action;
	protected String actionId;
	protected HttpServletRequest request;

	public SpringConversationAdapter(HttpServletRequest request, HandlerMethod handler, ConversationContextManager conversationContextManager) {
		this.sessionContext = SessionContextUtil.getSessionContext(request);
		this.requestContext = RequestContextUtil.getRequestContext(request);
		this.action = handler.getBean();
		this.actionId = handler.getMethod().getName();
		this.conversationContextManager = conversationContextManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAction() {
		return this.action;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getActionId() {
		return this.actionId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getRequestContext() {

		HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		if (!currentRequest.equals(this.request)) {
			this.request = currentRequest;
			requestContext = RequestContextUtil.getRequestContext(currentRequest);
		}

		return this.requestContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConversationContext beginConversation(String conversationName, long maxIdleTimeMillis) {
		return this.conversationContextManager.createContext(conversationName, maxIdleTimeMillis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConversationContext getConversationContext(String conversationName, String conversationId) {
		return this.conversationContextManager.getContext(conversationName, conversationId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConversationContext endConversation(String conversationName, String conversationId) {
		return this.conversationContextManager.remove(conversationName, conversationId);
	}

}
