package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.ComponentInitializationListener;
import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.ComponentStrategyFactory;
import com.github.overengineer.scope.container.PrototypeComponentStrategy;

import java.util.List;

/**
 */
public class ProxyComponentStrategyFactory implements ComponentStrategyFactory {

    private final ComponentStrategyFactory delegateFactory;
    private final ProxyHandlerFactory handlerFactory;

    public ProxyComponentStrategyFactory(ComponentStrategyFactory delegateFactory, ProxyHandlerFactory handlerFactory) {
        this.delegateFactory = delegateFactory;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public <T> ComponentStrategy<T> create(Class<T> implementationType, List<ComponentInitializationListener> initializationListeners) {
        ComponentStrategy<T> delegateStrategy = delegateFactory.create(implementationType, initializationListeners);
        if (delegateStrategy instanceof PrototypeComponentStrategy) {
            return new PrototypeProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
        }
        return new SingletonProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
    }

    @Override
    public <T> ComponentStrategy<T> createInstanceStrategy(T implementation, List<ComponentInitializationListener> initializationListeners) {
        return new SingletonProxyComponentStrategy<T>(implementation.getClass(), delegateFactory.createInstanceStrategy(implementation, initializationListeners), handlerFactory);
    }

    @Override
    public <T> ComponentStrategy<T> createDecoratorStrategy(Class<T> implementationType, List<ComponentInitializationListener> initializationListeners, Class<?> delegateClass, ComponentStrategy<?> decoratorDelegateStrategy) {
        ComponentStrategy<T> delegateStrategy = delegateFactory.createDecoratorStrategy(implementationType, initializationListeners, delegateClass, decoratorDelegateStrategy);
        if (delegateStrategy instanceof PrototypeComponentStrategy) {
            return new PrototypeProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
        }
        return new SingletonProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
    }
}
