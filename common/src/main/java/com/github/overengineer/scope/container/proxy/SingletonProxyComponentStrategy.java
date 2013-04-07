package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentInitializationListener;
import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.Provider;

import java.util.List;

/**
 */
public class SingletonProxyComponentStrategy<T> implements HotSwappableProxyStrategy<T> {

    private ComponentProxyHandler<T> proxyHandler;
    private Class<?> type;
    private ComponentStrategy<T> delegateStrategy;
    private ProxyHandlerFactory handlerFactory;

    public SingletonProxyComponentStrategy(Class<?> type, ComponentStrategy<T> delegateStrategy, ProxyHandlerFactory handlerFactory) {
        this.type = type;
        this.delegateStrategy = delegateStrategy;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public T get(Provider provider, List<ComponentInitializationListener> initializationListeners) {

        if (proxyHandler != null) {
            return proxyHandler.getProxy();
        }

        proxyHandler = handlerFactory.createProxy(type);

        T component = delegateStrategy.get(provider, initializationListeners);

        proxyHandler.setComponent(component);

        return proxyHandler.getProxy();

    }

    @Override
    public ComponentProxyHandler<T> getProxyHandler() {
        return proxyHandler;
    }

    @Override
    public void swap(ComponentProxyHandler<T> proxyHandler, Provider provider, List<ComponentInitializationListener> initializationListeners) {

        this.proxyHandler = proxyHandler;

        T component = delegateStrategy.get(provider, initializationListeners);

        proxyHandler.setComponent(component);

    }
}
