package com.github.overengineer.container.proxy;

import com.github.overengineer.container.*;
import com.github.overengineer.container.dynamic.DynamicComponentFactory;
import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.Locksmith;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.scope.Scopes;

import java.util.List;

/**
 * TODO the hot swapping doesnt use qualifiers
 *
 * @author rees.byars
 */
public class DefaultHotSwappableContainer extends DefaultContainer implements HotSwappableContainer {

    private final ComponentStrategyFactory strategyFactory;

    public DefaultHotSwappableContainer(ComponentStrategyFactory strategyFactory, DynamicComponentFactory dynamicComponentFactory, MetadataAdapter metadataAdapter, List<ComponentInitializationListener> componentInitializationListeners) {
        super(strategyFactory, dynamicComponentFactory, metadataAdapter, componentInitializationListeners);
        this.strategyFactory = strategyFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized  <T> void swap(Class<T> target, Class<? extends T> implementationType) throws HotSwapException {

        Key targetKey = Locksmith.makeKey(target);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) getStrategy(targetKey);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentStrategy.getComponentType(), implementationType);
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.create(implementationType, targetKey.getQualifier(), Scopes.SINGLETON);

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

        Key targetKey = Locksmith.makeKey(target);

        ComponentStrategy<T> currentStrategy = (ComponentStrategy<T>) getStrategy(targetKey);

        if (!(currentStrategy instanceof HotSwappableProxyStrategy)) {
            throw new HotSwapException(target, currentStrategy.getComponentType(), implementation.getClass());
        }

        ComponentProxyHandler<T> proxyHandler = ((HotSwappableProxyStrategy) currentStrategy).getProxyHandler();

        ComponentStrategy<T> newStrategy = (ComponentStrategy<T>) strategyFactory.createInstanceStrategy(implementation, targetKey.getQualifier());

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
