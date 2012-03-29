package com.google.code.rees.scope.conversation.context;

import java.io.Serializable;

/**
 * Uses a {@link ConversationContextFactory} to manage the creation, caching,
 * retrieval, removal, and expiration of conversations
 * 
 * @author rees.byars
 * 
 */
public interface ConversationContextManager extends Serializable {

    /**
     * 5 minutes
     */
    public static final long DEFAULT_MONITOR_FREQUENCY = 300000;

    /**
     * 20
     */
    public static final int DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION = 20;

    /**
     * Set the frequency with which {@link ConversationContext
     * ConversationContexts} will
     * be monitored for potential removal
     * 
     * @param frequency
     */
    public void setMonitoringFrequency(long frequency);

    /**
     * Set the max number of cached {@link ConversationContext
     * ConversationContexts}.
     * 
     * @param maxInstances
     */
    public void setMaxInstances(int maxInstances);

    /**
     * Set the {@link ConversationContextFactory} to be used by this manager
     * 
     * @param contextFactory
     */
    public void setContextFactory(ConversationContextFactory contextFactory);

    /**
     * Retrieve the context identified by the given information, creating
     * a new one if none exists.
     * 
     * @param conversationName
     * @param conversationId
     * @return
     */
    public ConversationContext getContext(String conversationName,
            String conversationId);

    /**
     * Remove the context identified by the given information, returning the
     * context
     * being removed
     * 
     * @param conversationName
     * @param conversationId
     * @return
     */
    public ConversationContext remove(String conversationName,
            String conversationId);

}
