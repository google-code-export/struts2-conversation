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
 *  $Id: ProgrammaticModelDrivenConversationSupport.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2.programmatic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerFactory;
import com.google.code.rees.scope.conversation.exceptions.ConversationException;
import com.google.code.rees.scope.conversation.processing.ConversationManager;
import com.google.code.rees.scope.struts2.StrutsConversationAdapter;
import com.google.code.rees.scope.struts2.StrutsScopeConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.inject.Inject;

/**
 * This class makes it simple to manage models with conversation-scoped
 * life-cycles programmatically.
 * 
 * All access to the model is through the {@link #getModel()} and
 * {@link #setModel(Serializable)} methods so that retrieval and insertion of
 * the model from and into conversation instances
 * can be managed on behalf of inheriting classes.
 * 
 * Use of this class requires zero configuration (no interceptors, etc.).
 * 
 * @author rees.byars
 * 
 * @param <T>
 */
public abstract class ProgrammaticModelDrivenConversationSupport<T extends Serializable> extends ActionSupport implements ProgrammaticModelDrivenConversation<T>, Preparable {

    private static final long serialVersionUID = -3567083451289146237L;

    private T model;
    protected long maxIdleTime;
	private ConversationManager conversationManager;
	private HttpConversationContextManagerFactory conversationContextManagerFactory;
    
    @Inject(StrutsScopeConstants.CONVERSATION_IDLE_TIMEOUT)
    public void setMaxIdleTime(long maxIdleTime) {
    	this.maxIdleTime = maxIdleTime;
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_MANAGER_KEY)
    public void setConversationManager(ConversationManager manager) {
        this.conversationManager = manager;
    }

    @Inject(StrutsScopeConstants.CONVERSATION_CONTEXT_MANAGER_FACTORY)
    public void setHttpConversationContextManagerFactory(HttpConversationContextManagerFactory conversationContextManagerFactory) {
        this.conversationContextManagerFactory = conversationContextManagerFactory;
    }

    /**
     * {@inheritDoc}
     * 
     * The model is scoped to the conversations indicated by
     * {@link #getConversations()}
     */
    @Override
    public T getModel() {
        if (this.model == null) {
            this.model = ProgrammaticModelDrivenConversationUtil.getModel(this, this.getModelName());
        }
        return this.model;
    }

    /**
     * {@inheritDoc}
     * 
     * The model is scoped to the conversations indicated by
     * {@link #getConversations()}
     */
    @Override
    public void setModel(T model) {
        ProgrammaticModelDrivenConversationUtil.setModel(model, this, this.getModelName());
        this.model = model;
    }

    /**
     * The name of the model used to identify it in the
     * {@link com.google.code.rees.scope.conversation.context.ConversationContext
     * ConversationContext}.
     * 
     * This can be overridden to provide the name of choice. The default is
     * <code>this.getClass().getName()</code>.
     * 
     * @return
     */
    protected String getModelName() {
        return this.getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    public void prepare() {
    	ActionContext actionContext = ActionContext.getContext();
    	HttpServletRequest request = (HttpServletRequest) actionContext.get(StrutsStatics.HTTP_REQUEST);
    	ConversationContextManager contextManager = this.conversationContextManagerFactory.getManager(request);
        try {
			this.conversationManager.processConversations(new StrutsConversationAdapter(actionContext.getActionInvocation(), contextManager));
			Map<String, Map<String, String>> stackItem = new HashMap<String, Map<String, String>>();
	        stackItem.put(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY, ConversationAdapter.getAdapter().getViewContext());
	        actionContext.getValueStack().push(stackItem);
        } catch (ConversationException e) {
			LOG.error("Programmatic Conversation error in Prepare method", e);
		}
    }

    /**
     * Begins new instances of this class's conversations
     */
    protected void beginConversations() {
        ProgrammaticModelDrivenConversationUtil.begin(this, this.maxIdleTime);
    }

    /**
     * Continues this class's conversations associated with the current request
     */
    protected void continueConversations() {
        ProgrammaticModelDrivenConversationUtil.persist(this);
    }

    /**
     * Ends this class's conversations associated with the current request
     */
    protected void endConversations() {
        ProgrammaticModelDrivenConversationUtil.end(this);
    }

}
