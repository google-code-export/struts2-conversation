package com.github.overengineer.scope.container.proxy;

/**
 */
public class JdkProxyHandlerFactory implements ProxyHandlerFactory{

    @Override
    public <T> ComponentProxyHandler<T> createProxy(Class<?> targetClass) {
        return new JdkComponentProxyHandler<T>(targetClass.getInterfaces());
    }

}
