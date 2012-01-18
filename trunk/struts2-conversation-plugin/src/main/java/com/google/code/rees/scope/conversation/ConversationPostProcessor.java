package com.google.code.rees.scope.conversation;

public interface ConversationPostProcessor {

	public void postProcessConversation(ConversationAdapter conversationAdapter, ConversationConfig conversationConfig, String conversationId);
	
}
