package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentInitializationListener;
import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.Provider;

import java.util.List;

/**
 */
public interface HotSwappableProxyStrategy<T> extends ComponentStrategy<T> {

    ComponentProxyHandler<T> getProxyHandler();

    public void swap(ComponentProxyHandler<T> proxyHandler, Provider provider, List<ComponentInitializationListener> initializationListeners);

}
