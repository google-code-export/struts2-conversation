package com.google.code.rees.scope.conversation;

import java.io.Serializable;

public interface ConversationManager extends Serializable {
	
	public void setConversationConfigBuilder(ConversationConfigBuilder configBuilder);
	public void processConversations(ConversationAdapter conversationAdapter);
	public void injectConversationFields(ConversationAdapter conversationAdapter);
	public void extractConversationFields(ConversationAdapter conversationAdapter);
	
}
