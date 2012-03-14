package com.google.code.rees.scope.conversation.context;

import java.io.Serializable;

/**
 * Creates instances of {@link ConversationContext ConversationContexts}
 * 
 * @author rees.byars
 * 
 */
public interface ConversationContextFactory extends Serializable {

    /**
     * 8 hours
     */
    public static final long DEFAULT_CONVERSATION_DURATION = 28800000;

    /**
     * Set the max-idle duration for all conversations created by this factory
     * 
     * @param duration
     */
    public void setConversationDuration(long duration);

    /**
     * Create a new context with the given name and id
     * 
     * @param conversationName
     * @param conversationId
     * @return
     */
    public ConversationContext create(String conversationName,
            String conversationId);
}
