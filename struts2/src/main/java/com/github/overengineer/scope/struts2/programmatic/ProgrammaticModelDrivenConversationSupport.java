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
package com.github.overengineer.scope.struts2.programmatic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.github.overengineer.scope.container.Provider;
import com.github.overengineer.scope.struts2.StrutsScopeContainerProvider;
import org.apache.struts2.StrutsStatics;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.ConversationConstants;
import com.github.overengineer.scope.conversation.context.ConversationContextManager;
import com.github.overengineer.scope.conversation.context.JeeConversationContextManagerProvider;
import com.github.overengineer.scope.conversation.exceptions.ConversationException;
import com.github.overengineer.scope.conversation.processing.ConversationProcessor;
import com.github.overengineer.scope.struts2.StrutsConversationAdapter;
import com.github.overengineer.scope.struts2.StrutsScopeConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.inject.Inject;

/**
 * This class makes it simple to manage models with conversation-scoped
 * life-cycles programmatically.
 * <p/>
 * All access to the model is through the {@link #getModel()} and
 * {@link #setModel(Serializable)} methods so that retrieval and insertion of
 * the model from and into conversation instances
 * can be managed on behalf of inheriting classes.
 * <p/>
 * Use of this class requires zero configuration (no interceptors, etc.).
 *
 * @param <T>
 * @author rees.byars
 */
public abstract class ProgrammaticModelDrivenConversationSupport<T extends Serializable> extends ActionSupport implements ProgrammaticModelDrivenConversation<T>, Preparable {

    private static final long serialVersionUID = -3567083451289146237L;

    private T model;
    protected Provider scopeContainer;

    @Inject
    public void setScopeContainerProvider(StrutsScopeContainerProvider scopeContainerProvider) {
        scopeContainer = scopeContainerProvider.getProvider();
    }

    /**
     * {@inheritDoc}
     * <p/>
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
     * <p/>
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
     * {@link com.github.overengineer.scope.conversation.context.ConversationContext
     * ConversationContext}.
     * <p/>
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
        ConversationContextManager contextManager = scopeContainer.get(JeeConversationContextManagerProvider.class).getManager(request);
        try {
            scopeContainer.get(ConversationProcessor.class).processConversations(new StrutsConversationAdapter(actionContext.getActionInvocation(), contextManager));
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
        ProgrammaticModelDrivenConversationUtil.begin(this,
                scopeContainer.getProperty(long.class, ConversationConstants.Properties.CONVERSATION_IDLE_TIMEOUT),
                scopeContainer.getProperty(int.class, ConversationConstants.Properties.CONVERSATION_MAX_INSTANCES));
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
