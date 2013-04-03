package com.github.overengineer.scope.container.proxy;

/**
 */
public interface ProxyHandlerFactory {

    <T> ComponentProxyHandler<T> createProxy(Class<?> targetClass);

}
