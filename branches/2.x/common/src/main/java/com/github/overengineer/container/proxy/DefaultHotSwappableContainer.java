package com.github.overengineer.container.proxy;

import com.github.overengineer.container.*;
import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.KeyGenerator;

/**
 * @author rees.byars
 */
public class DefaultHotSwappableContainer extends DefaultContainer implements HotSwappableContainer {

    public DefaultHotSwappableContainer(ComponentStrategyFactory strategyFactory, KeyGenerator keyGenerator) {
        super(strategyFactory, keyGenerator);
        addInstance(HotSwappableContainer.class, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void swap(Class<T> target, Class<? extends T> implementationType) throws HotSwapException {

        Key targetKey = keyGenerator.fromType(target);

        Class<?> currentImplementationType = mappings.get(targetKey);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) strategies.get(currentImplementationType);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementationType);
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.createDecoratorStrategy(implementationType, initializationListeners, currentImplementationType, currentStrategy);

        if (!(newStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementationType);
        }

        ((HotSwappableProxyStrategy) newStrategy).swap(proxyHandler, this);

        for (Container child : children) {
            if (child instanceof HotSwappableContainer) {
                ((HotSwappableContainer) child).swap(target, implementationType);
            }
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, I extends T> void swap(Class<T> target, I implementation) throws HotSwapException {

        Key targetKey = keyGenerator.fromType(target);

        Class<?> currentImplementationType = mappings.get(targetKey);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) strategies.get(currentImplementationType);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementation.getClass());
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.createInstanceStrategy(implementation, initializationListeners);

        if (!(newStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementation.getClass());
        }

        ((HotSwappableProxyStrategy) newStrategy).swap(proxyHandler, this);

        for (Container child : children) {
            if (child instanceof HotSwappableContainer) {
                ((HotSwappableContainer) child).swap(target, implementation);
            }
        }

    }
}
