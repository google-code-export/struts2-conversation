package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.*;

/**
 */
public class HotSwappableProxyContainer extends DefaultContainer implements HotSwappableContainer {

    public HotSwappableProxyContainer(ProxyComponentStrategyFactory strategyFactory) {
        super(strategyFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void swap(Class<T> target, Class<? extends T> implementationType) throws HotSwapException {

        Class<?> currentImplementationType = mappings.get(target);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) strategies.get(currentImplementationType);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementationType);
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.create(implementationType);

        if (!(newStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementationType);
        }

        ((HotSwappableProxyStrategy) newStrategy).swap(proxyHandler, this, initializationListeners);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, I extends T> void swap(Class<T> target, I implementation) throws HotSwapException {

        Class<?> currentImplementationType = mappings.get(target);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) strategies.get(currentImplementationType);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementation.getClass());
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.create(implementation);

        if (!(newStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementation.getClass());
        }

        ((HotSwappableProxyStrategy) newStrategy).swap(proxyHandler, this, initializationListeners);

    }
}
