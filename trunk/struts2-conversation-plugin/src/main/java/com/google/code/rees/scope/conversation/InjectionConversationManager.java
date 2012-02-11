package com.google.code.rees.scope.conversation;

public interface InjectionConversationManager extends ConversationManager {
    
    public void injectConversationFields(Object target, ConversationAdapter conversationAdapter);
    public void extractConversationFields(Object target, ConversationAdapter conversationAdapter);

}
