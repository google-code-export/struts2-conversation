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
 *  $Id: ScopeManager.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope;

import java.io.Serializable;

import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.session.SessionManager;

/**
 * Uses a {@link ConversationManager} and {@link SessionManager} to provide
 * external frameworks with central manager for
 * processing both the conversation and session scopes.
 * 
 * @author rees.byars
 */
public interface ScopeManager extends Serializable {

    /**
     * Sets the {@link ConversationManager} that this manager will use
     * 
     * @param manager
     */
    public void setConversationManager(ConversationManager manager);

    /**
     * Sets the {@link SessionManager} that this manager will use
     * 
     * @param manager
     */
    public void setSessionManager(SessionManager manager);

    /**
     * Sets the {@link ScopeAdapterFactory} that this manager will use
     * for generating scope adapters.
     * 
     * @param adapterFactory
     */
    public void setScopeAdapterFactory(ScopeAdapterFactory adapterFactory);

    /**
     * Performs the scope processing for the conversation and session scopes
     */
    public void processScopes();

}
