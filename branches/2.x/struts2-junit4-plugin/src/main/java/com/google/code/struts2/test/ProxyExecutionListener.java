package com.google.code.struts2.test;

import com.opensymphony.xwork2.ActionInvocation;

public interface ProxyExecutionListener {

    /**
     * Similar to a PreResultListener, but guaranteed to be called after all the
     * beforeResult methods of the PreResultListeners.
     *
     * @param invocation
     * @param result
     */
    public void afterProxyExecution(ActionInvocation invocation, String result);
}
