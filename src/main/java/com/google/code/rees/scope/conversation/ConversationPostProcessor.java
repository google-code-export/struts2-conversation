package com.google.code.rees.scope.conversation;

import java.io.Serializable;

/**
 * This interface allows for post-processing of a conversation.  Registered via
 * {@link ConversationAdapter#addPostProcessor(ConversationPostProcessor, ConversationConfiguration, String)
 * @author rees.byars
 */
public interface ConversationPostProcessor extends Serializable {

    /**
     * Perform the post-processing using the given parameters
     * 
     * @param conversationAdapter
     * @param conversationConfig
     * @param conversationId
     */
    public void postProcessConversation(
            ConversationAdapter conversationAdapter,
            ConversationConfiguration conversationConfig, String conversationId);

}
