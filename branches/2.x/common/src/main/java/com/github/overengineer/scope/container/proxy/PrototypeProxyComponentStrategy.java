package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.Provider;

/**
 * @author rees.byars
 */
public class PrototypeProxyComponentStrategy<T> implements ComponentStrategy<T> {

    private final Class<?> type;
    private final ComponentStrategy<T> delegateStrategy;
    private final ProxyHandlerFactory handlerFactory;
    private final ThreadLocal<ProxyHandlerHolder> handlerHolder = new ThreadLocal<ProxyHandlerHolder>();

    public PrototypeProxyComponentStrategy(Class<?> type, ComponentStrategy<T> delegateStrategy, ProxyHandlerFactory handlerFactory) {
        this.type = type;
        this.delegateStrategy = delegateStrategy;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public T get(Provider provider) {

        ProxyHandlerHolder holder = handlerHolder.get();

        if (holder != null) {
            return holder.proxyHandler.getProxy();
        }

        ComponentProxyHandler<T> proxyHandler = handlerFactory.createProxy(type);

        holder = new ProxyHandlerHolder();

        holder.proxyHandler = proxyHandler;

        try {

            handlerHolder.set(holder);

            T component = delegateStrategy.get(provider);

            proxyHandler.setComponent(component);

            return proxyHandler.getProxy();

        } finally {

            handlerHolder.remove();

        }

    }

    class ProxyHandlerHolder {
        ComponentProxyHandler<T> proxyHandler;
    }
}