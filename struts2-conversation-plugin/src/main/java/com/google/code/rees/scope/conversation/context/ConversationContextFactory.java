package com.google.code.rees.scope.conversation.context;

import java.io.Serializable;

public interface ConversationContextFactory extends Serializable {

    /**
     * 8 hours
     */
    public static final long DEFAULT_CONVERSATION_DURATION = 28800000;

    public void setConversationDuration(long duration);

    public ConversationContext create(String conversationId);
}
