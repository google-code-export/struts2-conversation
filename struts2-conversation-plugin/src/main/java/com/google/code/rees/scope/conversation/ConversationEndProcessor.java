package com.google.code.rees.scope.conversation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConversationPostProcessor} used to remove a conversation context
 * from the session context after an action has been executed.  Is registered
 * for execution via {@link ConversationAdapter#addPostProcessor(ConversationPostProcessor, ConversationConfiguration, String)
 * 
 * @author rees.byars
 * 
 */
public class ConversationEndProcessor implements ConversationPostProcessor {

    private static final long serialVersionUID = 2164492176500215404L;
    private static final Logger LOG = LoggerFactory
            .getLogger(ConversationEndProcessor.class);

    /**
     * Removes the conversation context from the session context
     */
    @Override
    public void postProcessConversation(
            ConversationAdapter conversationAdapter,
            ConversationConfiguration conversationConfig, String conversationId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("In Conversation "
                    + conversationConfig.getConversationName()
                    + ", removing conversation map from session following conversation end.");
        }
        conversationAdapter.endConversation(
                conversationConfig.getConversationName(), conversationId);
    }

}
