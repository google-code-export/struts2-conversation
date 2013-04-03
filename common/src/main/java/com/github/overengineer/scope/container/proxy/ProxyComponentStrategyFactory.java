package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.ComponentStrategyFactory;
import com.github.overengineer.scope.container.PrototypeComponentStrategy;

/**
 */
public class ProxyComponentStrategyFactory implements ComponentStrategyFactory {

    private ComponentStrategyFactory delegateFactory;
    private ProxyHandlerFactory handlerFactory;

    public ProxyComponentStrategyFactory(ComponentStrategyFactory delegateFactory, ProxyHandlerFactory handlerFactory) {
        this.delegateFactory = delegateFactory;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public <T> ComponentStrategy<T> create(Class<T> implementationType) {
        ComponentStrategy<T> delegateStrategy = delegateFactory.create(implementationType);
        if (delegateStrategy instanceof PrototypeComponentStrategy) {
            return new PrototypeProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
        }
        return new SingletonProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
    }

    @Override
    public <T> ComponentStrategy<T> create(T implementation) {
        return new SingletonProxyComponentStrategy<T>(implementation.getClass(), delegateFactory.create(implementation), handlerFactory);
    }

}
