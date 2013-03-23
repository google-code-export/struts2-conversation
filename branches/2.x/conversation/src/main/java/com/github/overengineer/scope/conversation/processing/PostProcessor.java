package com.github.overengineer.scope.conversation.processing;

import java.io.Serializable;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.configuration.ConversationClassConfiguration;

/**
 * processor to be executed after main conversation processing
 *
 * @author reesbyars
 */
public interface PostProcessor extends Serializable {

    /**
     * Perform the post-processing using the given parameters
     *
     * @param conversationAdapter
     * @param conversationConfig
     * @param conversationId
     */
    public void postProcessConversation(ConversationAdapter conversationAdapter, ConversationClassConfiguration conversationConfig, String conversationId);
}
