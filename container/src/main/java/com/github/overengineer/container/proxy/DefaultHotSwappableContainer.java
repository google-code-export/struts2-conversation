package com.github.overengineer.container.proxy;

import com.github.overengineer.container.*;
import com.github.overengineer.container.factory.MetaFactory;
import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.key.SerializableKey;

/**
 * @author rees.byars
 */
public class DefaultHotSwappableContainer extends DefaultContainer implements HotSwappableContainer {

    public DefaultHotSwappableContainer(ComponentStrategyFactory strategyFactory, KeyRepository keyRepository, MetaFactory metaFactory) {
        super(strategyFactory, keyRepository, metaFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void swap(Class<T> target, Class<? extends T> implementationType) throws HotSwapException {

        SerializableKey targetKey = keyRepository.retrieveKey(target);

        Class<?> currentImplementationType = mappings.get(targetKey);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) strategies.get(currentImplementationType);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementationType);
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.createDecoratorStrategy(implementationType, currentImplementationType, currentStrategy);

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

        SerializableKey targetKey = keyRepository.retrieveKey(target);

        Class<?> currentImplementationType = mappings.get(targetKey);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) strategies.get(currentImplementationType);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentImplementationType, implementation.getClass());
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.createInstanceStrategy(implementation);

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

    @Override
    public Container makeInjectable() {
        addInstance(HotSwappableContainer.class, this);
        return super.makeInjectable();
    }
}
