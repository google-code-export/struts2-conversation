package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.util.Map;

/**
 * Creates and returns a <code>Map<String, Object></code> Conversation Context
 * 
 * @author rees.byars
 */
public interface ConversationContextFactory extends Serializable {

    /**
     * Creates and returns a <code>Map<String, Object></code> Conversation
     * Context,
     * placing it in the given sessionContext with the conversationId as the key
     */
    public Map<String, Object> createConversationContext(String conversationId,
            Map<String, Object> sessionContext);

}
