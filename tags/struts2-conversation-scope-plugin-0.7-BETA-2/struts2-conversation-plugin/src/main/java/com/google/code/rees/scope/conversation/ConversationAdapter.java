package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class ConversationAdapter implements Serializable, ConversationContextFactory {
	
	private static final long serialVersionUID = -8006640931436858515L;
	protected static ThreadLocal<ConversationAdapter> conversationAdapter = new ThreadLocal<ConversationAdapter>();
	protected Map<String, String> viewContext;
	protected ConversationPostProcessorWrapperFactory postProcessorFactory;
	protected Collection<ConversationPostProcessorWrapper> postProcessors;
	
	public ConversationAdapter() {
		this.viewContext = new HashMap<String, String>();
		this.postProcessors = new HashSet<ConversationPostProcessorWrapper>();
		this.postProcessorFactory = new DefaultConversationPostProcessorWrapperFactory();
		conversationAdapter.set(this);
	}
	
	public abstract Object getAction();
	public abstract String getActionId();
	public abstract Map<String, Object> getSessionContext();
	public abstract Map<String, String> getRequestContext();
	
	public Map<String, String> getViewContext() {
		return this.viewContext;
	}
	
	public void addPostProcessor(ConversationPostProcessor postProcessor,
			ConversationConfiguration conversationConfig, String conversationId) {
		ConversationPostProcessorWrapper wrapper = this.postProcessorFactory.create(this, postProcessor, conversationConfig, conversationId);
		this.postProcessors.add(wrapper);
	}
	
	public void executePostProcessors() {
		for (ConversationPostProcessorWrapper postProcessor : this.postProcessors) {
			postProcessor.postProcessConversation();
		}
	}
	
	public static void setAdapter(ConversationAdapter adapter) {
		conversationAdapter.set(adapter);
	}
	
	public static ConversationAdapter getAdapter() {
		return conversationAdapter.get();
	}
}
