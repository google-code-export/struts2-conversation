package com.google.code.struts2.scope.conversation;

public interface ConversationManager {
	
	public void setConversationConfigBuilder(ConversationConfigBuilder configBuilder);
	public void processConversations(ConversationAdapter conversationAdapter);
	public void injectConversationFields(ConversationAdapter conversationAdapter);
	public void extractConversationFields(ConversationAdapter conversationAdapter);
	
}
