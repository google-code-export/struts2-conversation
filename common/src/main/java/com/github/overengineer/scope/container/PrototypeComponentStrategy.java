package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.inject.CompositeInjector;
import com.github.overengineer.scope.container.instantiate.Instantiator;

import java.util.List;

/**
 */
public class PrototypeComponentStrategy<T> implements ComponentStrategy<T> {

    private final CompositeInjector<T> injector;
    private final Instantiator<T> instantiator;
    private final List<ComponentInitializationListener> initializationListeners;

    public PrototypeComponentStrategy(CompositeInjector<T> injector, Instantiator<T> instantiator, List<ComponentInitializationListener> initializationListeners) {
        this.injector = injector;
        this.instantiator = instantiator;
        this.initializationListeners = initializationListeners;
    }

    @Override
    public T get(Provider provider) {
        T component = instantiator.getInstance(provider);
        injector.inject(component, provider);
        if (component instanceof PostConstructable) {
            ((PostConstructable) component).init();
        }
        for (ComponentInitializationListener listener : initializationListeners) {
            component = listener.onInitialization(component);
        }
        return component;
    }
}
