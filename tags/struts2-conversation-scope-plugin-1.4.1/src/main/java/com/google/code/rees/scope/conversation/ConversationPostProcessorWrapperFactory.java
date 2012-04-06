package com.google.code.rees.scope.conversation;

import java.io.Serializable;

/**
 * Creates {@link ConversationPostProcessorWrapper
 * ConversationPostProcessorWrappers}
 * 
 * @author rees.byars
 * 
 */
public interface ConversationPostProcessorWrapperFactory extends Serializable {
    /**
     * Returns an instance of a {@link ConversationPostProcessorWrapper}
     * 
     * @param conversationAdapter
     * @param postProcessor
     * @param conversationConfig
     * @param conversationId
     * @return
     */
    public ConversationPostProcessorWrapper create(
            ConversationAdapter conversationAdapter,
            ConversationPostProcessor postProcessor,
            ConversationConfiguration conversationConfig, String conversationId);
}
