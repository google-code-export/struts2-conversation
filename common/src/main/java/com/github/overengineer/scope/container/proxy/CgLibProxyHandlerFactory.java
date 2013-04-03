package com.github.overengineer.scope.container.proxy;

/**
 */
public class CgLibProxyHandlerFactory implements ProxyHandlerFactory {
    @Override
    public <T> ComponentProxyHandler<T> createProxy(Class<?> targetClass) {
        return new CgLibComponentProxyHandler<T>(targetClass);
    }
}
