package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.Provider;

/**
 */
public interface HotSwappableProxyStrategy<T> extends ComponentStrategy<T> {

    ComponentProxyHandler<T> getProxyHandler();

    public void swap(ComponentProxyHandler<T> proxyHandler, Provider provider);

}
