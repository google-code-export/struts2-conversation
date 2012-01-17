package com.google.code.struts2.scope.conversation;

import com.opensymphony.xwork2.ActionInvocation;

public interface ConversationManager {
	
	public void processConversations(ActionInvocation invocation);
	public void injectConversationFields(Object action);
	public void extractConversationFields(Object action);
	
}
