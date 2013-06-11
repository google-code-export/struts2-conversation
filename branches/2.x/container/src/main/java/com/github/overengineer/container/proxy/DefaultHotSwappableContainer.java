package com.github.overengineer.container.proxy;

import com.github.overengineer.container.*;
import com.github.overengineer.container.dynamic.DynamicComponentFactory;
import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.scope.Scopes;

import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultHotSwappableContainer extends DefaultContainer implements HotSwappableContainer {

    private final ComponentStrategyFactory strategyFactory;
    private final KeyRepository keyRepository;

    public DefaultHotSwappableContainer(ComponentStrategyFactory strategyFactory, KeyRepository keyRepository, DynamicComponentFactory dynamicComponentFactory, MetadataAdapter metadataAdapter, List<ComponentInitializationListener> componentInitializationListeners) {
        super(strategyFactory, keyRepository, dynamicComponentFactory, metadataAdapter, componentInitializationListeners);
        this.strategyFactory = strategyFactory;
        this.keyRepository = keyRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized  <T> void swap(Class<T> target, Class<? extends T> implementationType) throws HotSwapException {

        Key targetKey = keyRepository.retrieveKey(target);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) getStrategy(targetKey);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentStrategy.getComponentType(), implementationType);
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.create(implementationType, Scopes.SINGLETON);

        if (!(newStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, newStrategy.getComponentType(), implementationType);
        }

        ((HotSwappableProxyStrategy) newStrategy).swap(proxyHandler, this);

        for (Container child : getChildren()) {
            if (child instanceof HotSwappableContainer) {
                ((HotSwappableContainer) child).swap(target, implementationType);
            }
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized  <T, I extends T> void swap(Class<T> target, I implementation) throws HotSwapException {

        Key targetKey = keyRepository.retrieveKey(target);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) getStrategy(targetKey);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentStrategy.getComponentType(), implementation.getClass());
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.createInstanceStrategy(implementation);

        if (!(newStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, newStrategy.getComponentType(), implementation.getClass());
        }

        ((HotSwappableProxyStrategy) newStrategy).swap(proxyHandler, this);

        for (Container child : getChildren()) {
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
