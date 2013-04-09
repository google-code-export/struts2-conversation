package com.github.overengineer.scope.container.proxy;

public class SimpleProxyHandler<T> implements ComponentProxyHandler<T> {

    private T component;
    private T proxy;

    public SimpleProxyHandler(T proxy) {
        this.proxy = proxy;
    }

    @Override
    public T getProxy() {
        return proxy;
    }

    @Override
    public void setComponent(T component) {
        this.component = component;
    }

}
