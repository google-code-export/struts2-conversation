package com.google.code.rees.scope.conversation;

import java.io.Serializable;

public interface ConversationPostProcessor extends Serializable {

	public void postProcessConversation(ConversationAdapter conversationAdapter, ConversationConfiguration conversationConfig, String conversationId);
	
}
