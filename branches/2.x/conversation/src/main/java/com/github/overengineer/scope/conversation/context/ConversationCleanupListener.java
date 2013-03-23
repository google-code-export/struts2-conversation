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
 * $Id: ConversationCleanupListener.java Apr 20, 2012 11:32:39 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.github.overengineer.scope.conversation.context;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class listens for the destruction of sessions and cleans up that sessions conversation resources.
 * With Servlet API 3.0+, this class is auto-discovered using the {@link WebListener} annotation. In earlier
 * API versions, it must be configured in the web.xml (or not at all if cleanup is not a concern).
 *
 * @author rees.byars
 */
@WebListener
public class ConversationCleanupListener implements HttpSessionListener {

    private static Logger LOG = LoggerFactory.getLogger(ConversationCleanupListener.class);

    public ConversationCleanupListener() {
        LOG.info("Conversation Cleanup Listener created.");
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //do nothing - do not create the manager here as it may not be needed
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ConversationContextManager contextManager = JeeConversationUtil.getContextManager(se.getSession());
        if (contextManager != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cleaning up conversation resources for session with ID [{}]", se.getSession().getId());
            }
            contextManager.destroy();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cleanup of conversation resources completed for session with ID [{}]", se.getSession().getId());
            }
        }
    }

}
