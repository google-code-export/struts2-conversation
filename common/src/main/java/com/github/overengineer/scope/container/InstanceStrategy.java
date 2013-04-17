package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.inject.CompositeInjector;

import java.util.List;

/**
 * @author rees.byars
 */
public class InstanceStrategy<T> implements ComponentStrategy<T> {

    private T instance;
    private final CompositeInjector<T> injector;
    private final List<ComponentInitializationListener> initializationListeners;
    private boolean initialized = false;

    public InstanceStrategy(T instance, CompositeInjector<T> injector, List<ComponentInitializationListener> initializationListeners) {
        this.instance = instance;
        this.injector = injector;
        this.initializationListeners = initializationListeners;
    }

    @Override
    public T get(Provider provider) {
        if (!initialized) {
            injector.inject(instance, provider);
            if (instance instanceof PostConstructable) {
                ((PostConstructable) instance).init();
            }
            for (ComponentInitializationListener listener : initializationListeners) {
                instance = listener.onInitialization(instance);
            }
            initialized = true;
        }
        return instance;
    }
}
