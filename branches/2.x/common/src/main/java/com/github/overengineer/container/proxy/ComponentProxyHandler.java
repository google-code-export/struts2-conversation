package com.github.overengineer.container.proxy;


import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ComponentProxyHandler<T> extends Serializable {

    T getProxy();

    T getComponent();

    void setComponent(T component);

}
