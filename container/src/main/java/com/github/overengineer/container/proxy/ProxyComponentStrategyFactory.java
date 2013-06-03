package com.github.overengineer.container.proxy;

import com.github.overengineer.container.ComponentStrategy;
import com.github.overengineer.container.ComponentStrategyFactory;
import com.github.overengineer.container.PrototypeComponentStrategy;
import com.github.overengineer.container.util.ReflectionUtil;

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
        ComponentStrategy<T> delegateStrategy = delegateFactory.createInstanceStrategy(implementation);
        if (ReflectionUtil.isPropertyType(implementation.getClass())) {
            return delegateStrategy;
        }
        return new SingletonProxyComponentStrategy<T>(implementation.getClass(), delegateStrategy, handlerFactory);
    }

    @Override
    public <T> ComponentStrategy<T> createCustomStrategy(ComponentStrategy providerStrategy) {
        ComponentStrategy<T> customStrategy = delegateFactory.createCustomStrategy(providerStrategy);
        return new SingletonProxyComponentStrategy<T>(customStrategy.getComponentType(), customStrategy, handlerFactory);
    }
}
