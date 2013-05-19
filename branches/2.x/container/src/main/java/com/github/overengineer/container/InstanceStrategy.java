package com.github.overengineer.container;

import com.github.overengineer.container.inject.ComponentInjector;
import com.github.overengineer.container.metadata.PostConstructable;

import java.util.List;

/**
 * @author rees.byars
 */
public class InstanceStrategy<T> implements ComponentStrategy<T> {

    private T instance;
    private final ComponentInjector<T> injector;
    private final List<ComponentInitializationListener> initializationListeners;
    private boolean initialized = false;

    public InstanceStrategy(T instance, ComponentInjector<T> injector, List<ComponentInitializationListener> initializationListeners) {
        this.instance = instance;
        this.injector = injector;
        this.initializationListeners = initializationListeners;
    }

    @Override
    public T get(Provider provider) {
        if (!initialized) {
            synchronized (this) {
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
            }
        }
        return instance;
    }

    @Override
    public Class getProvidedType() {
        return instance.getClass();
    }
}
