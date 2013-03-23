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
 * $Id: TimeoutConversationContextManager.java Apr 19, 2012 6:27:47 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.github.overengineer.scope.conversation.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.container.Component;
import com.github.overengineer.scope.container.Prototype;
import com.github.overengineer.scope.monitor.TimeoutListener;
import com.github.overengineer.scope.monitor.TimeoutMonitor;

/**
 * This class extends the {@link DefaultConversationContextManager} and uses a {@link TimeoutMonitor} to
 * monitor conversations for timeouts.  Using the {@link TimeoutListener} interface, it removes conversations
 * from its cache when they have expired.
 *
 * @author rees.byars
 */
@Prototype
public class TimeoutConversationContextManager extends DefaultConversationContextManager implements TimeoutListener<ConversationContext> {

    private static final long serialVersionUID = -4431057690602876686L;

    private static final Logger LOG = LoggerFactory.getLogger(TimeoutConversationContextManager.class);

    protected TimeoutMonitor<ConversationContext> conversationTimeoutMonitor;

    @Component
    public void setTimeoutMonitor(TimeoutMonitor<ConversationContext> conversationTimeoutMonitor) {
        this.conversationTimeoutMonitor = conversationTimeoutMonitor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext createContext(String conversationName, long maxIdleTimeMillis, int maxInstances) {
        ConversationContext context = super.createContext(conversationName, maxIdleTimeMillis, maxInstances);
        context.addTimeoutListener(this);
        this.conversationTimeoutMonitor.addTimeoutable(context);
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext remove(String conversationName, String conversationId) {
        ConversationContext context = super.remove(conversationName, conversationId);
        if (context != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Conversation [{}] with ID [{}] has ended, removing from TimeoutMonitor", conversationName, conversationId);
            }
            this.conversationTimeoutMonitor.removeTimeoutable(context);
        }
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTimeout(ConversationContext expiredConversation) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Conversation [{}] with ID [{}] has timed out", expiredConversation.getConversationName(), expiredConversation.getId());
        }
        super.remove(expiredConversation.getConversationName(), expiredConversation.getId());
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        LOG.debug("Destroying TimeoutConversationContextManager.");
        super.destroy();
        LOG.debug("Destroying TimeoutMonitor.");
        this.conversationTimeoutMonitor.destroy();
        LOG.debug("TimeoutConversationContextManager and TimeoutMonitor destroyed.");
    }

}
