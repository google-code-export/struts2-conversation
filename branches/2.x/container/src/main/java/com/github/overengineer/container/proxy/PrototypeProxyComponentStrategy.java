package com.github.overengineer.container.proxy;

import com.github.overengineer.container.ComponentStrategy;
import com.github.overengineer.container.Provider;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author rees.byars
 */
public class PrototypeProxyComponentStrategy<T> implements ComponentStrategy<T> {

    private final Class<?> type;
    private final ComponentStrategy<T> delegateStrategy;
    private final ProxyHandlerFactory handlerFactory;
    private transient ThreadLocal<ProxyHandlerHolder> handlerHolder = new ThreadLocal<ProxyHandlerHolder>();

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

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        handlerHolder = new ThreadLocal<ProxyHandlerHolder>();
    }

    class ProxyHandlerHolder {
        ComponentProxyHandler<T> proxyHandler;
    }
}
