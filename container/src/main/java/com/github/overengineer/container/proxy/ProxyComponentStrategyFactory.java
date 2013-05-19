package com.github.overengineer.container.proxy;

import com.github.overengineer.container.ComponentStrategy;
import com.github.overengineer.container.ComponentStrategyFactory;
import com.github.overengineer.container.PrototypeComponentStrategy;

/**
 * @author rees.byars
 */
public class ProxyComponentStrategyFactory implements ComponentStrategyFactory {

    private final ComponentStrategyFactory delegateFactory;
    private final ProxyHandlerFactory handlerFactory;

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
    public <T> ComponentStrategy<T> createInstanceStrategy(T implementation) {
        return new SingletonProxyComponentStrategy<T>(implementation.getClass(), delegateFactory.createInstanceStrategy(implementation), handlerFactory);
    }

    @Override
    public <T> ComponentStrategy<T> createDecoratorStrategy(Class<T> implementationType, ComponentStrategy<?> decoratorDelegateStrategy) {
        ComponentStrategy<T> delegateStrategy = delegateFactory.createDecoratorStrategy(implementationType, decoratorDelegateStrategy);
        if (delegateStrategy instanceof PrototypeComponentStrategy) {
            return new PrototypeProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
        }
        return new SingletonProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
    }
}
