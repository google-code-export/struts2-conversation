package com.github.overengineer.scope.container.alt;

import com.github.overengineer.scope.container.PostConstructable;
import com.github.overengineer.scope.container.Provider;

import java.util.List;

/**
 */
public class InstanceStrategy<T> implements ComponentStrategy<T> {

    private T instance;
    private CompositeInjector<T> injector;
    private boolean initialized = false;

    public InstanceStrategy(T instance, CompositeInjector<T> injector) {
        this.instance = instance;
        this.injector = injector;
    }

    @Override
    public T get(Provider provider, List<ComponentInitializationListener> initializationListeners) {
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
