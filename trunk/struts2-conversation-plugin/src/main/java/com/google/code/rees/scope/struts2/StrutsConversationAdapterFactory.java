package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.opensymphony.xwork2.ActionInvocation;

public interface StrutsConversationAdapterFactory {
	public ConversationAdapter create(ActionInvocation invocation);
}
