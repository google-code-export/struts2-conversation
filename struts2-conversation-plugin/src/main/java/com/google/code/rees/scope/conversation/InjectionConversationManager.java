package com.google.code.rees.scope.conversation;

public interface InjectionConversationManager {

    void injectConversationFields(Object target,
            ConversationAdapter conversationAdapter);

    void extractConversationFields(Object target,
            ConversationAdapter conversationAdapter);

}
