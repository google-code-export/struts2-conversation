package com.google.code.rees.scope.conversation;

import java.io.Serializable;

/**
 * The primary conversation processing and management component.
 * 
 * @author rees.byars
 * 
 */
public interface ConversationManager extends Serializable {

    /**
     * Set the configuration provider for this manager
     * 
     * @param configurationProvider
     */
    public void setConfigurationProvider(
            ConversationConfigurationProvider configurationProvider);

    /**
     * Process the conversations for the current request using the given adapter
     * 
     * @param conversationAdapter
     */
    public void processConversations(ConversationAdapter conversationAdapter);

}
