package com.google.code.rees.scope.conversation;

public class DefaultConversationPostProcessorWrapperFactory implements ConversationPostProcessorWrapperFactory {

	private static final long serialVersionUID = -8906181370354593161L;

	@Override
	public ConversationPostProcessorWrapper create(
			ConversationAdapter conversationAdapter,
			ConversationPostProcessor postProcessor,
			ConversationConfiguration conversationConfig, String conversationId) {
		return new DefaultConversationPostProcessorWrapper(conversationAdapter, postProcessor, conversationConfig, conversationId);
	}

}
