package com.google.code.rees.scope.conversation;

import com.google.code.rees.scope.conversation.annotations.ConversationField;

/**
 * Implementations of this class should implement injection of
 * {@link ConversationField ConversationFields}
 * 
 * @author rees.byars
 */
public interface InjectionConversationManager extends ConversationManager {

    /**
     * Inject the {@link ConversationField ConversationFields} on the target
     * from the appropriate conversation contexts for active conversations
     * associated with the current request
     * 
     * @param target
     * @param conversationAdapter
     */
    void injectConversationFields(Object target,
            ConversationAdapter conversationAdapter);

    /**
     * Extract the {@link ConversationField ConversationFields} from the target
     * and place them into the conversations' contexts for active conversations
     * associated with the current request.
     * 
     * @param target
     * @param conversationAdapter
     */
    void extractConversationFields(Object target,
            ConversationAdapter conversationAdapter);

}
