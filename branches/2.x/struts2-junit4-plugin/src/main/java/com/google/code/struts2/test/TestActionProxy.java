package com.google.code.struts2.test;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public class TestActionProxy implements ActionProxy {

    private List<ProxyExecutionListener> listeners = new ArrayList<ProxyExecutionListener>();
    private ActionProxy parentProxy;

    public TestActionProxy(ActionProxy parentProxy) {
        this.parentProxy = parentProxy;
    }

    /**
     * {@inheritDoc}
     */
    public void addExecutionListener(ProxyExecutionListener listener) {
        listeners.add(listener);
    }

    @Override
    public String execute() throws Exception {
        String result = this.parentProxy.execute();
        ActionInvocation invocation = this.parentProxy.getInvocation();
        for (ProxyExecutionListener listener : listeners) {
            listener.afterProxyExecution(invocation, result);
        }
        return result;
    }

    @Override
    public Object getAction() {
        return this.parentProxy.getAction();
    }

    @Override
    public String getActionName() {
        return this.parentProxy.getActionName();
    }

    @Override
    public ActionConfig getConfig() {
        return this.parentProxy.getConfig();
    }

    @Override
    public boolean getExecuteResult() {
        return this.parentProxy.getExecuteResult();
    }

    @Override
    public ActionInvocation getInvocation() {
        return this.parentProxy.getInvocation();
    }

    @Override
    public String getMethod() {
        return this.parentProxy.getMethod();
    }

    @Override
    public String getNamespace() {
        return this.parentProxy.getNamespace();
    }

    @Override
    public void setExecuteResult(boolean execute) {
        this.parentProxy.setExecuteResult(execute);
    }

    @Override
    public boolean isMethodSpecified() {
        return this.parentProxy.isMethodSpecified();
    }

}
