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
 *  $Id: DefaultJeeConversationContextManagerProvider.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.conversation.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.container.Component;
import com.github.overengineer.scope.container.ScopeContainer;

/**
 * The default implementation of the
 * {@link JeeConversationContextManagerProvider}
 * 
 * @author rees.byars
 * 
 */
public class DefaultJeeConversationContextManagerProvider implements JeeConversationContextManagerProvider {

    private static final long serialVersionUID = 1500381458203865515L;

    private static Logger LOG = LoggerFactory.getLogger(DefaultJeeConversationContextManagerProvider.class);

    protected ScopeContainer scopeContainer;
    
    @Component
	public void setScopeContainer(ScopeContainer scopeContainer) {
		this.scopeContainer = scopeContainer;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContextManager getManager(HttpServletRequest request) { 
    	HttpSession session = request.getSession();
    	ConversationContextManager contextManager = JeeConversationUtil.getContextManager(session);
        if (contextManager == null) {
        	contextManager = this.createContextManager(session);
        }
        return contextManager;
    }
    
    protected ConversationContextManager createContextManager(HttpSession session) {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("Creating new ConversationContextManager for session with ID:  " + session.getId());
    	}
    	ConversationContextManager contextManager = scopeContainer.getComponent(ConversationContextManager.class);
        JeeConversationUtil.setContextManager(session, contextManager);
        return contextManager;
    }

}
