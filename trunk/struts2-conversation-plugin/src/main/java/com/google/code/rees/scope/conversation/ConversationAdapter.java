package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.util.Map;

public abstract class ConversationAdapter implements Serializable {
	
	private static final long serialVersionUID = -8006640931436858515L;
	protected static ThreadLocal<ConversationAdapter> conversationAdapter = new ThreadLocal<ConversationAdapter>();
	
	public ConversationAdapter() {
		conversationAdapter.set(this);
	}
	
	public abstract Object getAction();
	public abstract String getActionId();
	public abstract Map<String, Object> getSessionContext();
	public abstract Map<String, String> getRequestContext();
	public abstract void dispatchPostProcessor(ConversationPostProcessor postProcessor, ConversationConfiguration conversationConfig, String conversationId);
	public abstract void addConversation(String conversationName, String conversationId);
	
	public static void setAdapter(ConversationAdapter adapter) {
		conversationAdapter.set(adapter);
	}
	
	public static ConversationAdapter getAdapter() {
		return conversationAdapter.get();
	}
}
