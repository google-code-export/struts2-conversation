package com.google.code.rees.scope.conversation;

import java.io.Serializable;

public interface ConversationManager extends Serializable {

    public void setConfigurationProvider(
            ConversationConfigurationProvider configurationProvider);

    public void processConversations(ConversationAdapter conversationAdapter);

}
