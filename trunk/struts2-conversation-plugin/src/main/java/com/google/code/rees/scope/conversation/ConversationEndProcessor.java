package com.google.code.rees.scope.conversation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationEndProcessor implements ConversationPostProcessor {

    private static final long serialVersionUID = 2164492176500215404L;
    private static final Logger LOG = LoggerFactory
            .getLogger(ConversationEndProcessor.class);

    @Override
    public void postProcessConversation(
            ConversationAdapter conversationAdapter,
            ConversationConfiguration conversationConfig, String conversationId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("In Conversation "
                    + conversationConfig.getConversationName()
                    + ", removing conversation map from session following conversation end.");
        }
        conversationAdapter.getSessionContext().remove(conversationId);
    }

}
