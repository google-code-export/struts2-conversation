package com.google.code.struts2.scope.testutil;

import com.google.code.struts2.test.junit.StrutsTest;
import com.opensymphony.xwork2.ActionInvocation;

public abstract class ScopeTestCase<T> extends StrutsTest<T> {
	
	@Override
	public void afterProxyExecution(ActionInvocation paramActionInvocation, String paramString) {
		
	}

}
