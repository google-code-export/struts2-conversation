package com.github.overengineer.scope.container.proxy;


import java.io.Serializable;

/**
 */
public interface ComponentProxyHandler<T> extends Serializable {

    T getProxy();

    T getComponent();

    void setComponent(T component);

}
