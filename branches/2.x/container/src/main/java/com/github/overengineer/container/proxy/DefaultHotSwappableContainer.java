package com.github.overengineer.container.proxy;

import com.github.overengineer.container.*;
import com.github.overengineer.container.factory.MetaFactory;
import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.key.SerializableKey;

import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultHotSwappableContainer extends DefaultContainer implements HotSwappableContainer {

    public DefaultHotSwappableContainer(ComponentStrategyFactory strategyFactory, KeyRepository keyRepository, MetaFactory metaFactory, List<ComponentInitializationListener> componentInitializationListeners) {
        super(strategyFactory, keyRepository, metaFactory, componentInitializationListeners);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized  <T> void swap(Class<T> target, Class<? extends T> implementationType) throws HotSwapException {

        SerializableKey targetKey = keyRepository.retrieveKey(target);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) strategies.get(targetKey);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentStrategy.getProvidedType(), implementationType);
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.createDecoratorStrategy(implementationType, currentStrategy);

        if (!(newStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, newStrategy.getProvidedType(), implementationType);
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
    public synchronized  <T, I extends T> void swap(Class<T> target, I implementation) throws HotSwapException {

        SerializableKey targetKey = keyRepository.retrieveKey(target);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) strategies.get(targetKey);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentStrategy.getProvidedType(), implementation.getClass());
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.createInstanceStrategy(implementation);

        if (!(newStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, newStrategy.getProvidedType(), implementation.getClass());
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