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
 *  $Id: StrutsConversationAdapter.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.util.RequestContextUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * Struts2 implementation of the {@link ConversationAdapter}.
 * 
 * @author rees.byars
 */
public class StrutsConversationAdapter extends ConversationAdapter {

    private static final long serialVersionUID = -907192380776385729L;

    protected ConversationContextManager conversationContextManager;
    protected ActionInvocation invocation;
    protected ActionContext actionContext;
    protected HttpServletRequest request;
    protected Map<String, String> requestContext;

    public StrutsConversationAdapter(ActionInvocation invocation, ConversationContextManager conversationContextManager) {
        this.invocation = invocation;
        this.actionContext = invocation.getInvocationContext();
        this.conversationContextManager = conversationContextManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAction() {
        return invocation.getAction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getActionId() {
        return invocation.getProxy().getMethod();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getRequestContext() {
    	if (this.requestContext == null) {
    		HttpServletRequest currentRequest = ((HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST));
            if (!currentRequest.equals(this.request)) {
                this.request = currentRequest;
                requestContext = RequestContextUtil.getRequestContext(currentRequest);
            }
    	}
        return requestContext;
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
