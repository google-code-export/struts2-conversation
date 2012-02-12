package com.google.code.rees.scope.struts2.test;

import com.google.code.struts2.test.junit.StrutsTest;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * This test case should not currently be used as it depends on an as
 * yet unavailable Struts2 testing library. Sorry!
 * 
 * @author rees.byars
 */
public abstract class StrutsScopeTestCase<T> extends StrutsTest<T> {

    @Override
    public void afterProxyExecution(ActionInvocation invocation, String result) {
        ScopeTestUtil.injectScopeFields(this);
        super.afterProxyExecution(invocation, result);
    }
}
