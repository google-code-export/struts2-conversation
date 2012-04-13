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
 *  $Id: StrutsScopeAdapterFactory.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.google.code.rees.scope.ScopeAdapterFactory;
import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerFactory;
import com.google.code.rees.scope.session.SessionAdapter;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Struts2 implementation of the {@link ScopeAdapterFactory}.
 * 
 * @author rees.byars
 */
public class StrutsScopeAdapterFactory implements ScopeAdapterFactory {

    private static final long serialVersionUID = -4595690103120891078L;
    protected HttpConversationContextManagerFactory conversationContextManagerFactory;

    @Inject(StrutsScopeConstants.CONVERSATION_CONTEXT_MANAGER_FACTORY)
    public void setHttpConversationContextManagerFactory(
            HttpConversationContextManagerFactory conversationContextManagerFactory) {
        this.conversationContextManagerFactory = conversationContextManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SessionAdapter createSessionAdapter() {
        return new StrutsSessionAdapter(ActionContext.getContext()
                .getActionInvocation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationAdapter createConversationAdapter() {
        ActionContext actionContext = ActionContext.getContext();
        HttpServletRequest request = (HttpServletRequest) actionContext
                .get(StrutsStatics.HTTP_REQUEST);
        ConversationContextManager contextManager = this.conversationContextManagerFactory
                .getManager(request);
        ActionInvocation invocation = actionContext.getActionInvocation();
        return new StrutsConversationAdapter(invocation, contextManager);
    }

}
