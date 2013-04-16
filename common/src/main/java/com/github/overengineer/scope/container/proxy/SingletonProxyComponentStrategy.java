package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.Provider;

/**
 */
public class SingletonProxyComponentStrategy<T> implements HotSwappableProxyStrategy<T> {

    private ComponentProxyHandler<T> proxyHandler;
    private final Class<?> type;
    private final ComponentStrategy<T> delegateStrategy;
    private final ProxyHandlerFactory handlerFactory;

    public SingletonProxyComponentStrategy(Class<?> type, ComponentStrategy<T> delegateStrategy, ProxyHandlerFactory handlerFactory) {
        this.type = type;
        this.delegateStrategy = delegateStrategy;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public T get(Provider provider) {

        if (proxyHandler != null) {
            return proxyHandler.getProxy();
        }

        proxyHandler = handlerFactory.createProxy(type);

        T component = delegateStrategy.get(provider);

        proxyHandler.setComponent(component);

        return proxyHandler.getProxy();

    }

    @Override
    public ComponentProxyHandler<T> getProxyHandler() {
        return proxyHandler;
    }

    @Override
    public void swap(ComponentProxyHandler<T> proxyHandler, Provider provider) {

        this.proxyHandler = proxyHandler;

        T component = delegateStrategy.get(provider);

        proxyHandler.setComponent(component);

    }
}
