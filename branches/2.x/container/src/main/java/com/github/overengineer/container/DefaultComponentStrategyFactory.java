package com.github.overengineer.container;

import com.github.overengineer.container.inject.CompositeInjector;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.instantiate.*;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.metadata.NativeScope;

import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultComponentStrategyFactory implements ComponentStrategyFactory {

    private final MetadataAdapter metadataAdapter;
    private final InjectorFactory injectorFactory;
    private final InstantiatorFactory instantiatorFactory;
    private final List<ComponentInitializationListener> initializationListeners;

    public DefaultComponentStrategyFactory(MetadataAdapter metadataAdapter, InjectorFactory injectorFactory, InstantiatorFactory instantiatorFactory, List<ComponentInitializationListener> initializationListeners) {
        this.metadataAdapter = metadataAdapter;
        this.injectorFactory = injectorFactory;
        this.instantiatorFactory = instantiatorFactory;
        this.initializationListeners = initializationListeners;
    }

    @Override
    public <T> ComponentStrategy<T> create(Class<T> implementationType) {
        CompositeInjector<T> injector = injectorFactory.create(implementationType);
        Instantiator<T> instantiator = instantiatorFactory.create(implementationType);
        if (NativeScope.PROTOTYPE.equals(metadataAdapter.getScope(implementationType))) {
            return new PrototypeComponentStrategy<T>(injector, instantiator, initializationListeners);
        } else {
            return new SingletonComponentStrategy<T>(new PrototypeComponentStrategy<T>(injector, instantiator, initializationListeners));
        }
    }

    @Override
    public <T> ComponentStrategy<T> createInstanceStrategy(T implementation) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) implementation.getClass();
        CompositeInjector<T> injector = injectorFactory.create(clazz);
        return new InstanceStrategy<T>(implementation, injector, initializationListeners);
    }

    @Override
    public <T> ComponentStrategy<T> createDecoratorStrategy(Class<T> implementationType, Class<?> delegateClass, ComponentStrategy<?> delegateStrategy) {
        CompositeInjector<T> injector = injectorFactory.create(implementationType);
        Instantiator<T> instantiator = instantiatorFactory.create(implementationType, delegateClass, delegateStrategy);
        if (NativeScope.PROTOTYPE.equals(metadataAdapter.getScope(implementationType))) {
            return new PrototypeComponentStrategy<T>(injector, instantiator, initializationListeners);
        } else {
            return new SingletonComponentStrategy<T>(new PrototypeComponentStrategy<T>(injector, instantiator, initializationListeners));
        }
    }

}
