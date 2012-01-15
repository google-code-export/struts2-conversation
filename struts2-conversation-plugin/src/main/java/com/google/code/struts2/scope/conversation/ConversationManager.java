package com.google.code.struts2.scope.conversation;

import java.util.Set;

import com.opensymphony.xwork2.ActionInvocation;

public interface ConversationManager {
	
	public void processConversationFields(ActionInvocation invocation);
	public void injectConversationFields(Object action);
	public void extractConversationFields(Object action);
	public Set<String> getAllConversationNames();
	
}
