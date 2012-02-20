package com.google.code.rees.scope.conversation.context;

import java.io.Serializable;

public interface ConversationContextManager extends Serializable {

    /**
     * 5 minutes
     */
    public static final long DEFAULT_MONITOR_FREQUENCY = 300000;

    public static final int DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION = 20;

    public void setMonitoringFrequency(long frequency);

    public void setMaxInstances(int maxInstances);

    public void setContextFactory(ConversationContextFactory contextFactory);

    public ConversationContext getContext(String conversationName,
            String conversationId);

    public ConversationContext remove(String conversationName,
            String conversationId);

}
