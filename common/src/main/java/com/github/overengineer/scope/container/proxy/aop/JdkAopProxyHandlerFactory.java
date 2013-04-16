package com.github.overengineer.scope.container.proxy.aop;

import com.github.overengineer.scope.container.proxy.ComponentProxyHandler;
import com.github.overengineer.scope.container.proxy.DefaultJdkProxyFactory;
import com.github.overengineer.scope.container.proxy.JdkProxyFactory;
import com.github.overengineer.scope.container.proxy.ProxyHandlerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class JdkAopProxyHandlerFactory implements ProxyHandlerFactory {

    private final Map<Class<?>, JdkProxyFactory> proxyFactories = new HashMap<Class<?>, JdkProxyFactory>();
    private final JoinPointInvocationFactory invocationFactory;

    public JdkAopProxyHandlerFactory(JoinPointInvocationFactory invocationFactory) {
        this.invocationFactory = invocationFactory;
    }

    @Override
    public <T> ComponentProxyHandler<T> createProxy(Class<?> targetClass) {
        JdkProxyFactory proxyFactory = proxyFactories.get(targetClass);
        if (proxyFactory == null) {
            proxyFactory = new DefaultJdkProxyFactory(targetClass);
            proxyFactories.put(targetClass, proxyFactory);
        }
        return new JdkAopProxyHandler<T>(proxyFactory, invocationFactory);
    }
}
