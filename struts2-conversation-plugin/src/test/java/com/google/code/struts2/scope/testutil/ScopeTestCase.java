package com.google.code.struts2.scope.testutil;

import java.util.Map;
import java.util.Map.Entry;

import com.google.code.struts2.scope.ScopeUtil;
import com.google.code.struts2.scope.conversation.ConversationConstants;
import com.google.code.struts2.test.junit.StrutsTest;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

public abstract class ScopeTestCase<T> extends StrutsTest<T> {
	
	@Override
	public void afterProxyExecution(ActionInvocation paramActionInvocation, String paramString) {
		ScopeUtil.injectScopeFields(this);
	}
	
	public void addConversationIdsToRequest() {
		@SuppressWarnings("unchecked")
		Map<String, String> convoIdMap = ((Map<String, String>) ActionContext.getContext().getValueStack()
				.findValue(ConversationConstants.CONVERSATION_ID_MAP_STACK_KEY));
		if (convoIdMap != null) {
			for (Entry<String, String> entry : convoIdMap.entrySet()) {
				request.addParameter(entry.getKey(), entry.getValue());
			}
		}
	}

}
