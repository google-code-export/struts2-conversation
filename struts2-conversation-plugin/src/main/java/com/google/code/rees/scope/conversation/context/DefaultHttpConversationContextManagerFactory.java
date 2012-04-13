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
 *  $Id: DefaultHttpConversationContextManagerFactory.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.code.rees.scope.conversation.ConversationConstants;

/**
 * The default implementation of the
 * {@link HttpConversationContextManagerFactory}
 * 
 * @author rees.byars
 * 
 */
public class DefaultHttpConversationContextManagerFactory implements
        HttpConversationContextManagerFactory {

    private static final long serialVersionUID = 1500381458203865515L;

    /**
     * {@inheritDoc}
     * 
     * The {@link ConversationContextManager} returned is a
     * {@link DefaultConversationContextManager}
     */
    @Override
    public ConversationContextManager getManager(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object ctxMgr = null;
        ctxMgr = session
                .getAttribute(ConversationConstants.CONVERSATION_CONTEXT_MANAGER_KEY);
        if (ctxMgr == null) {
            ctxMgr = new DefaultConversationContextManager();
            session.setAttribute(
                    ConversationConstants.CONVERSATION_CONTEXT_MANAGER_KEY,
                    ctxMgr);
        }
        return (ConversationContextManager) ctxMgr;
    }

}
