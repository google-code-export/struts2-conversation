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
 *  $Id: DefaultConversationContextManager.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.conversation.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.container.Component;

/**
 * The default implementation of the {@link ConversationContextManager}.
 *
 * @author rees.byars
 */
public class DefaultConversationContextManager implements ConversationContextManager {

    private static final long serialVersionUID = 3699451038473294837L;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultConversationContextManager.class);

    protected ConversationContextFactory contextFactory;
    protected final Map<String, Map<String, ConversationContext>> conversations = Collections.synchronizedMap(new HashMap<String, Map<String, ConversationContext>>());
    protected AtomicLong nextId = new AtomicLong();

    @Component
    public void setContextFactory(ConversationContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext createContext(String conversationName, long maxIdleTimeMillis, int maxInstances) {

        ConversationContext context = null;

        synchronized (this.conversations) {

            Map<String, ConversationContext> conversationContexts = this.conversations.get(conversationName);

            if (conversationContexts == null) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Creating new context cache for [{}]", conversationName);
                }

                conversationContexts = Collections.synchronizedMap(new HashMap<String, ConversationContext>());
                this.conversations.put(conversationName, conversationContexts);

            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating new ConversationContext for [{}]", conversationName);
            }

            String conversationId = this.getNextId();
            context = this.contextFactory.create(conversationName, conversationId, maxIdleTimeMillis);
            conversationContexts.put(conversationId, context);

            if (conversationContexts.size() > maxInstances) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cached instances of conversation [{}] exceeds limit.  Removing stale conversations.", conversationName);
                }

                this.removeMostStaleConversation(conversationContexts, maxInstances, conversationName, context.getRemainingTime());

            }

        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext getContext(String conversationName, String conversationId) {

        ConversationContext context = null;

        synchronized (this.conversations) {

            Map<String, ConversationContext> conversationContexts = this.conversations.get(conversationName);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Retrieving cached instance for conversation [{}]", conversationName);
            }

            if (conversationContexts != null) {
                context = conversationContexts.get(conversationId);
                if (context != null) {
                    context.reset(); //reset the timeout
                }
            }

        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext remove(String conversationName, String conversationId) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Discarding [{}] with ID [{}]", conversationName, conversationId);
        }

        ConversationContext context = null;

        Map<String, ConversationContext> conversationContexts = this.conversations.get(conversationName);

        if (conversationContexts != null) {
            context = conversationContexts.remove(conversationId);
        }

        return context;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {

        LOG.debug("Destroying ConversationContextManager and clearing conversation cache.");

        synchronized (this.conversations) {

            this.conversations.clear();

        }

        LOG.debug("ConversationContextManager destroyed and conversation cache cleared.");

    }

    /**
     * Recursively removes the least-recently accessed conversations
     */
    protected void removeMostStaleConversation(Map<String, ConversationContext> conversationContexts, int maxInstances, String conversationName, long defaultDuration) {

        String mostStaleId = null;
        long leastRemainingTime = defaultDuration;

        synchronized (conversationContexts) {
            for (Entry<String, ConversationContext> entry : conversationContexts.entrySet()) {

                long entryRemainingTime = entry.getValue().getRemainingTime();

                if (entryRemainingTime <= leastRemainingTime) {

                    mostStaleId = entry.getKey();
                    leastRemainingTime = entryRemainingTime;

                }
            }

            this.remove(conversationName, mostStaleId);
        }


        if (LOG.isDebugEnabled()) {
            LOG.debug("Discarding most stale " + conversationName + " context with ID " + mostStaleId);
            LOG.debug("Remaining " + conversationName + " contexts for this session:  " + conversationContexts.size());
        }

        if (conversationContexts.size() > maxInstances) {
            removeMostStaleConversation(conversationContexts, maxInstances, conversationName, defaultDuration);
        }

    }

    protected String getNextId() {
        return String.valueOf(this.nextId.getAndIncrement());
    }

}
