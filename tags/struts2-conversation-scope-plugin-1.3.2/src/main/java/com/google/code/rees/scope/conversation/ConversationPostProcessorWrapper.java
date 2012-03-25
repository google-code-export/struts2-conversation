package com.google.code.rees.scope.conversation;

import java.io.Serializable;

/**
 * A wrapper class for {@link ConversationPostProcessor}. Allows for
 * {@link ConversationPostProcessor#postProcessConversation(ConversationAdapter, ConversationConfiguration, String)}
 * to be called without the caller needing to provide the parameters
 * 
 * @see {@link ConversationPostProcessorWrapperFactory}
 * @author rees.byars
 * 
 */
public interface ConversationPostProcessorWrapper extends Serializable {

    /**
     * Calls
     * {@link ConversationPostProcessor#postProcessConversation(ConversationAdapter, ConversationConfiguration, String)}
     * for an underlying post processor
     */
    public void postProcessConversation();

}
