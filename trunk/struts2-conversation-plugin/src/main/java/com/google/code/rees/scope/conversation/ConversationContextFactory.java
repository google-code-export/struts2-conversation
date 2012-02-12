package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.util.Map;

public interface ConversationContextFactory extends Serializable {

    public Map<String, Object> createConversationContext(String conversationId,
            Map<String, Object> sessionContext);

}
