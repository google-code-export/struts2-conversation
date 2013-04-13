package com.github.overengineer.scope.container.proxy;

/**
 */
public interface InvocationHandler<T> extends ComponentProxyHandler<T> {

    void setInvocationFactory(InvocationFactory invocationFactory);

}
