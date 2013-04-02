package com.github.overengineer.scope.container.proxy;


/**
 */
public interface ComponentProxyHandler<T> {

    T getProxy();

    void setComponent(T component);

}
