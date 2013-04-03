package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentInitializationListener;
import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.Provider;
import com.github.overengineer.scope.container.WiringException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ProxyComponentStrategy<T> implements ComponentStrategy<T> {

    private Class<?> type;
    private ComponentStrategy<T> delegateStrategy;
    private ProxyHandlerFactory handlerFactory;

    public ProxyComponentStrategy(Class<?> type, ComponentStrategy<T> delegateStrategy, ProxyHandlerFactory handlerFactory) {
        this.type = type;
        this.delegateStrategy = delegateStrategy;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public T get(Provider provider, List<ComponentInitializationListener> initializationListeners) {

        ProxyCache cache = ProxyCache.threadLocal.get();

        if (cache == null) {
            cache = new ProxyCache();
            ProxyCache.threadLocal.set(cache);
        }

        @SuppressWarnings("unchecked")
        ComponentProxyHandler<T> proxyHandler = (ComponentProxyHandler<T>) cache.proxyHandlers.get(type);

        if (proxyHandler != null) {
            return proxyHandler.getProxy();
        }

        try {

            proxyHandler = handlerFactory.createProxy(type);

            cache.proxyHandlers.put(type, proxyHandler);

            T component = delegateStrategy.get(provider, initializationListeners);

            proxyHandler.setComponent(component);

            cache.proxyHandlers.remove(type);

            return proxyHandler.getProxy();

        } catch (Exception e) {

            ProxyCache.threadLocal.remove();

            throw new RuntimeException(new WiringException("An error occurred creating proxy and component for type [" + type.getName() + "]", e));

        } finally {

            if (cache.proxyHandlers.isEmpty()) {
                ProxyCache.threadLocal.remove();
            }

        }

    }

    private static class ProxyCache {
        static ThreadLocal<ProxyCache> threadLocal = new ThreadLocal<ProxyCache>();
        Map<Class<?>, ComponentProxyHandler<?>> proxyHandlers = new HashMap<Class<?>, ComponentProxyHandler<?>>();
    }
}
