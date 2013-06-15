package com.github.overengineer.container.proxy;

import com.github.overengineer.container.ComponentStrategy;
import com.github.overengineer.container.ComponentStrategyFactory;
import com.github.overengineer.container.PrototypeComponentStrategy;
import com.github.overengineer.container.scope.Scope;
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
    public <T> ComponentStrategy<T> create(Class<T> implementationType, Object qualifier, Scope scope) {
        ComponentStrategy<T> delegateStrategy = delegateFactory.create(implementationType, qualifier, scope);
        if (ReflectionUtil.getAllInterfaces(implementationType).size() == 0) {
            return delegateStrategy;
        }
        if (delegateStrategy instanceof PrototypeComponentStrategy) {
            return new PrototypeProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
        }
        return new SingletonProxyComponentStrategy<T>(implementationType, delegateStrategy, handlerFactory);
    }

    @Override
    public <T> ComponentStrategy<T> createInstanceStrategy(T implementation, Object qualifier) {
        ComponentStrategy<T> delegateStrategy = delegateFactory.createInstanceStrategy(implementation, qualifier);
        Class<?> implementationType = implementation.getClass();
        if (ReflectionUtil.getAllInterfaces(implementationType).size() == 0 || ReflectionUtil.isPropertyType(implementationType)) {
            return delegateStrategy;
        }
        return new SingletonProxyComponentStrategy<T>(implementation.getClass(), delegateStrategy, handlerFactory);
    }

    @Override
    public <T> ComponentStrategy<T> createCustomStrategy(ComponentStrategy providerStrategy, Object qualifier) {
        ComponentStrategy<T> customStrategy = delegateFactory.createCustomStrategy(providerStrategy, qualifier);
        if (ReflectionUtil.getAllInterfaces(customStrategy.getComponentType()).size() == 0) {
            return customStrategy;
        }
        return new SingletonProxyComponentStrategy<T>(customStrategy.getComponentType(), customStrategy, handlerFactory);
    }
}
