package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.ComponentStrategyFactory;

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
        return new ProxyComponentStrategy<T>(implementationType, delegateFactory.create(implementationType), handlerFactory);
    }

    @Override
    public <T> ComponentStrategy<T> create(T implementation) {
        return new ProxyComponentStrategy<T>(implementation.getClass(), delegateFactory.create(implementation), handlerFactory);
    }

}
