package com.google.code.struts2.scope.conversation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ConversationAdapter {
	
	public ConversationAdapter() {
		conversationAdapter.set(this);
	}
	protected static ThreadLocal<ConversationAdapter> conversationAdapter = new ThreadLocal<ConversationAdapter>();
	public abstract Object getAction();
	public abstract String getActionId();
	public abstract Map<String, Object> getSessionContext();
	public abstract HttpServletRequest getRequest();
	public abstract HttpServletResponse getResponse();
	public abstract void dispatchPostProcessor(ConversationPostProcessor postProcessor, ConversationConfig conversationConfig, String conversationId);
	public abstract void addConversation(String conversationName, String conversationId);
	public static void setAdapter(ConversationAdapter adapter) {
		conversationAdapter.set(adapter);
	}
	public static ConversationAdapter getAdapter() {
		return conversationAdapter.get();
	}
}
