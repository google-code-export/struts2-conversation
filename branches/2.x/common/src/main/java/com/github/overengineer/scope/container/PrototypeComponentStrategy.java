package com.github.overengineer.scope.container;

import java.util.List;

/**
 */
public class PrototypeComponentStrategy<T> implements ComponentStrategy<T> {

    private CompositeInjector<T> injector;
    private Instantiator<T> instantiator;

    public PrototypeComponentStrategy(CompositeInjector<T> injector, Instantiator<T> instantiator) {
        this.injector = injector;
        this.instantiator = instantiator;
    }

    @Override
    public T get(Provider provider, List<ComponentInitializationListener> initializationListeners) {
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
