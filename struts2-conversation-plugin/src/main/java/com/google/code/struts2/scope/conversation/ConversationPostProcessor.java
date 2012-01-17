package com.google.code.struts2.scope.conversation;

public interface ConversationPostProcessor {

	public void postProcessConversation(ConversationAdapter conversationAdapter, ConversationConfig conversationConfig, String conversationId);
	
}
