package com.google.code.rees.scope.conversation;

public interface ConversationManager {
	
	public void setConversationConfigBuilder(ConversationConfigBuilder configBuilder);
	public void processConversations(ConversationAdapter conversationAdapter);
	public void injectConversationFields(ConversationAdapter conversationAdapter);
	public void extractConversationFields(ConversationAdapter conversationAdapter);
	
}
