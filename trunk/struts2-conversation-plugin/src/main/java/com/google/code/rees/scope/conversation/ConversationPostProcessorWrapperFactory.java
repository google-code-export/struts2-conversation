package com.google.code.rees.scope.conversation;

import java.io.Serializable;

public interface ConversationPostProcessorWrapperFactory extends Serializable {
    public ConversationPostProcessorWrapper create(
            ConversationAdapter conversationAdapter,
            ConversationPostProcessor postProcessor,
            ConversationConfiguration conversationConfig, String conversationId);
}
