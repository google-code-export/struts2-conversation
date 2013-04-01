package com.github.overengineer.scope.container;

import java.util.List;

/**
 */
public class SingletonComponentStrategy<T> implements ComponentStrategy<T> {

    private T component;
    private ComponentStrategy<T> delegateStrategy;

    public SingletonComponentStrategy(CompositeInjector<T> injector, Instantiator<T> instantiator) {
        delegateStrategy = new PrototypeComponentStrategy<T>(injector, instantiator);
    }

    @Override
    public T get(Provider provider, List<ComponentInitializationListener> initializationListeners) {
         if (component == null) {
             component = delegateStrategy.get(provider, initializationListeners);
         }
        return component;
    }

}
