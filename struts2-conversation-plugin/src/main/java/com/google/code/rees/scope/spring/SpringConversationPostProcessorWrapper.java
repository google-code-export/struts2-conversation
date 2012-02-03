package com.google.code.rees.scope.spring;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationConfiguration;
import com.google.code.rees.scope.conversation.ConversationPostProcessor;

public class SpringConversationPostProcessorWrapper {
	
	private ConversationConfiguration conversationConfig;
	private String conversationId;
	private ConversationPostProcessor postProcessor;
	private ConversationAdapter conversationAdapter;

	public SpringConversationPostProcessorWrapper(ConversationAdapter conversationAdapter,
			ConversationPostProcessor postProcessor,
			ConversationConfiguration conversationConfig, String conversationId) {
		this.conversationAdapter = conversationAdapter;
		this.postProcessor = postProcessor;
		this.conversationConfig = conversationConfig;
		this.conversationId = conversationId;
	}
	
	public void postProcessConversation() {
		this.postProcessor.postProcessConversation(conversationAdapter, conversationConfig, conversationId);
	}

}
