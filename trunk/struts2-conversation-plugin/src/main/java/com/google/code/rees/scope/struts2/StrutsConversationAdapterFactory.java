package com.google.code.rees.scope.struts2;

import java.io.Serializable;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.opensymphony.xwork2.ActionInvocation;

public interface StrutsConversationAdapterFactory extends Serializable{
	public ConversationAdapter create(ActionInvocation invocation);
}
