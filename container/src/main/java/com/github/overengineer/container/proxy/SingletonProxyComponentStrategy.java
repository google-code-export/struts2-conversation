package com.github.overengineer.container.proxy;

import com.github.overengineer.container.ComponentStrategy;
import com.github.overengineer.container.Provider;

/**
 * @author rees.byars
 */
public class SingletonProxyComponentStrategy<T> implements HotSwappableProxyStrategy<T> {

    private volatile ComponentProxyHandler<T> proxyHandler;
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

        if (proxyHandler == null) {

            synchronized (this) {

                if (proxyHandler == null) {

                    proxyHandler = handlerFactory.createProxy(type);

                    T component = delegateStrategy.get(provider);

                    proxyHandler.setComponent(component);

                }

            }

        }

        return proxyHandler.getProxy();

    }

    @Override
    public Class getComponentType() {
        return delegateStrategy.getComponentType();
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
