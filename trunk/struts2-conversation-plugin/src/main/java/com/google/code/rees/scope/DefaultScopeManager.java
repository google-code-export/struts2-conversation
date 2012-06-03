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
 *  $Id: DefaultScopeManager.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope;

import com.google.code.rees.scope.conversation.ConversationException;
import com.google.code.rees.scope.conversation.processing.ConversationManager;
import com.google.code.rees.scope.session.SessionManager;

/**
 * The default implementation of the {@link ScopeManager}.
 * 
 * @author rees.byars
 */
public class DefaultScopeManager implements ScopeManager {

    private static final long serialVersionUID = -7042031513311747101L;

    protected SessionManager sessionManager;
    protected ConversationManager conversationManager;
    protected ScopeAdapterFactory adapterFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConversationManager(ConversationManager manager) {
        this.conversationManager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSessionManager(SessionManager manager) {
        this.sessionManager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScopeAdapterFactory(ScopeAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    /**
     * {@inheritDoc}
     * @throws ConversationException 
     */
    @Override
    public void processScopes() throws ConversationException {
        this.sessionManager.processSessionFields(this.adapterFactory.createSessionAdapter());
        this.conversationManager.processConversations(this.adapterFactory.createConversationAdapter());
    }

}
