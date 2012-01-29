package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.opensymphony.xwork2.ActionInvocation;

public class DefaultStrutsConversationAdapterFactory implements StrutsConversationAdapterFactory {

	private static final long serialVersionUID = -6252607393095146545L;

	@Override
	public ConversationAdapter create(ActionInvocation invocation) {
		return new StrutsConversationAdapter(invocation);
	}

}
