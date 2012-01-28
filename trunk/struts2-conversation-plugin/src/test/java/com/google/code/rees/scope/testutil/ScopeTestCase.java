package com.google.code.rees.scope.testutil;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.struts2.TestStrutsConversationAdapter;
import com.google.code.rees.scope.struts2.test.ScopeTestUtil;
import com.google.code.struts2.test.junit.StrutsSpringTest;
import com.opensymphony.xwork2.ActionInvocation;

public abstract class ScopeTestCase<T> extends StrutsSpringTest<T> {

	@Override
	protected void preProxySetup() {
		super.preProxySetup();
		ConversationAdapter.setAdapter(new TestStrutsConversationAdapter(request, session));
	}
	
	@Override
	public void afterProxyExecution(ActionInvocation invocation, String result) {
		ScopeTestUtil.injectScopeFields(this);
		super.afterProxyExecution(invocation, result);
	}
}
