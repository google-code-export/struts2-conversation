package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentStrategy;

/**
 */
public interface HotSwappableProxyStrategy<T> extends ComponentStrategy<T> {

    ComponentProxyHandler<T> getProxyHandler();

    void setProxyHandler(ComponentProxyHandler<T> proxyHandler);

}
