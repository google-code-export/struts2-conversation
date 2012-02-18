package com.google.code.rees.scope.conversation.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConversationContextManager implements
        ConversationContextManager {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultConversationContextManager.class);

    private static final long serialVersionUID = 3699451038473294837L;

    protected ConversationContextFactory contextFactory = new DefaultConversationContextFactory();
    protected Map<String, Map<String, ConversationContext>> conversations = new HashMap<String, Map<String, ConversationContext>>();
    protected long monitoringFrequency = 5000;// DEFAULT_MONITOR_FREQUENCY;
    protected int maxInstances = DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION;
    protected Timer timer = new Timer();

    @Override
    public void setMonitoringFrequency(long frequency) {
        this.monitoringFrequency = frequency;
    }

    @Override
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    @Override
    public void setContextFactory(ConversationContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

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
            context = this.contextFactory.create(conversationId);
            conversationContexts.put(conversationId, context);
            this.timer.scheduleAtFixedRate(new ContextMonitor(
                    conversationContexts, context), this.monitoringFrequency,
                    this.monitoringFrequency);
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
                context = this.contextFactory.create(conversationId);
                conversationContexts.put(conversationId, context);
                this.timer.scheduleAtFixedRate(new ContextMonitor(
                        conversationContexts, context),
                        this.monitoringFrequency, this.monitoringFrequency);
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

        return context;
    }

    @Override
    public ConversationContext remove(String conversationName,
            String conversationId) {

        Map<String, ConversationContext> conversationContexts = this.conversations
                .get(conversationName);

        ConversationContext context = null;
        if (conversationContexts != null) {
            context = conversationContexts.remove(conversationId);
        }

        return context;

    }

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
        conversationContexts.remove(mostStaleId);
        if (conversationContexts.size() > this.maxInstances) {
            removeMostStaleConversation(conversationContexts, defaultDuration);
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("Monitoring conversation context with ID "
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
