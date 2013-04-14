package com.github.overengineer.scope.container.proxy;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class JdkProxyHandlerFactory implements ProxyHandlerFactory {

    private Map<Class<?>, JdkProxyFactory> proxyFactories = new HashMap<Class<?>, JdkProxyFactory>();

    @Override
    public <T> ComponentProxyHandler<T> createProxy(Class<?> targetClass) {
        JdkProxyFactory proxyFactory = proxyFactories.get(targetClass);
        if (proxyFactory == null) {
            proxyFactory = new DefaultJdkProxyFactory(targetClass);
            proxyFactories.put(targetClass, proxyFactory);
        }
        return new JdkComponentProxyHandler<T>(proxyFactory);
    }

}
