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
package com.google.code.rees.scope.conversation.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of the {@link ConversationContextManager}.
 * 
 * @author rees.byars
 * 
 */
public class DefaultConversationContextManager implements
        ConversationContextManager {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultConversationContextManager.class);

    private static final long serialVersionUID = 3699451038473294837L;

    protected ConversationContextFactory contextFactory = new DefaultConversationContextFactory();
    protected Map<String, Map<String, ConversationContext>> conversations = new HashMap<String, Map<String, ConversationContext>>();
    protected long monitoringFrequency = DEFAULT_MONITOR_FREQUENCY;
    protected int maxInstances = DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION;
    protected transient Timer timer = new Timer();

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMonitoringFrequency(long frequency) {
        this.monitoringFrequency = frequency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContextFactory(ConversationContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext getContext(String conversationName,
            String conversationId) {

        Map<String, ConversationContext> conversationContexts = this.conversations
                .get(conversationName);
        ConversationContext context = null;
        if (conversationContexts == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating new conversation instance cache and instance for conversation "
                        + conversationName);
            }
            conversationContexts = new HashMap<String, ConversationContext>();
            context = this.contextFactory.create(conversationName,
                    conversationId);
            conversationContexts.put(conversationId, context);
            this.conversations.put(conversationName, conversationContexts);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Retrieving cached instance for conversation "
                        + conversationName);
            }
            context = conversationContexts.get(conversationId);
            if (context == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No instance of " + conversationName
                            + " found.  Creating new instance.");
                }
                context = this.contextFactory.create(conversationName,
                        conversationId);
                conversationContexts.put(conversationId, context);
            }
            if (conversationContexts.size() > this.maxInstances) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cached instances of conversation "
                            + conversationName
                            + " exceeds limit.  Removing stale conversations.");
                }
                this.removeMostStaleConversation(conversationContexts,
                        context.getRemainingTime());
            }
        }

        // following block ensures monitoring continues after
        // serialization/deserialization
        TimerTask monitor = context.getTimerTask();
        if (monitor == null) {
            monitor = new ContextMonitor(conversationContexts, context);
            context.setTimerTask(monitor);
            if (timer == null) {
                this.createTimer();
            } else {
                this.timer.scheduleAtFixedRate(monitor,
                        this.monitoringFrequency, this.monitoringFrequency);
            }
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext remove(String conversationName,
            String conversationId) {

        Map<String, ConversationContext> conversationContexts = this.conversations
                .get(conversationName);

        ConversationContext context = null;
        if (conversationContexts != null) {
            context = conversationContexts.remove(conversationId);
            TimerTask task = context.getTimerTask();
            if (task != null) {
                task.cancel();
            }
        }

        return context;

    }

    /**
     * Recursively removes the least-recently accessed conversations until the
     * number of remaining
     * conversations equals {@link #maxInstances}
     */
    protected void removeMostStaleConversation(
            Map<String, ConversationContext> conversationContexts,
            long defaultDuration) {
        String mostStaleId = null;
        long leastRemainingTime = defaultDuration;
        for (Entry<String, ConversationContext> entry : conversationContexts
                .entrySet()) {
            long entryRemainingTime = entry.getValue().getRemainingTime();
            if (entryRemainingTime <= leastRemainingTime) {
                mostStaleId = entry.getKey();
                leastRemainingTime = entryRemainingTime;
            }
        }
        ConversationContext discardedContext = conversationContexts
                .remove(mostStaleId);
        TimerTask task = discardedContext.getTimerTask();
        if (task != null) {
            task.cancel();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Discarding most stale "
                    + discardedContext.getConversationName()
                    + "conversation context with ID "
                    + discardedContext.getId());
            LOG.debug("Remaining " + discardedContext.getConversationName()
                    + " conversation contexts for this session:  "
                    + conversationContexts.size());
        }
        if (conversationContexts.size() > this.maxInstances) {
            removeMostStaleConversation(conversationContexts, defaultDuration);
        }
    }

    /**
     * For use in case of the timer becoming null do to serialization. Creates
     * a new timer and adds the existing cached conversations' monitors to the
     * timer
     */
    protected synchronized void createTimer() {
        if (timer == null) {
            timer = new Timer();
            for (Map<String, ConversationContext> conversationContexts : this.conversations
                    .values()) {
                for (ConversationContext context : conversationContexts
                        .values()) {
                    this.timer.scheduleAtFixedRate(context.getTimerTask(),
                            this.monitoringFrequency, this.monitoringFrequency);
                }
            }
        }
    }

    class ContextMonitor extends TimerTask {

        private Map<String, ConversationContext> conversationContexts;
        private ConversationContext context;

        ContextMonitor(Map<String, ConversationContext> conversationContexts,
                ConversationContext context) {
            this.conversationContexts = conversationContexts;
            this.context = context;
        }

        @Override
        public void run() {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Monitoring conversation context with ID "
                        + context.getId());
            }
            if (!conversationContexts.containsValue(context)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Conversation has already been ended, ceasing monitoring of conversation context with ID "
                            + context.getId());
                }
                this.cancel();
            } else if (!context.isActive()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Due to inactivity, removing conversation with ID "
                            + context.getId());
                }
                conversationContexts.remove(context.getId());
                this.cancel();
            }
        }
    }

}
