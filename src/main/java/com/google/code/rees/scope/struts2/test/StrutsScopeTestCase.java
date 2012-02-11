package com.google.code.rees.scope.struts2.test;

import com.google.code.struts2.test.junit.StrutsTest;
import com.opensymphony.xwork2.ActionInvocation;

public abstract class StrutsScopeTestCase<T> extends StrutsTest<T> {
	
	@Override
	public void afterProxyExecution(ActionInvocation invocation, String result) {
		ScopeTestUtil.injectScopeFields(this);
		super.afterProxyExecution(invocation, result);
	}
}
