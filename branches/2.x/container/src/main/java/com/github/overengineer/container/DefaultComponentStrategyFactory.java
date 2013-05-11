package com.github.overengineer.container;

import com.github.overengineer.container.inject.CompositeInjector;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.instantiate.DecoratorInstantiator;
import com.github.overengineer.container.instantiate.DefaultInstantiator;
import com.github.overengineer.container.instantiate.Instantiator;

import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultComponentStrategyFactory implements ComponentStrategyFactory {

    private final InjectorFactory injectorFactory;

    public DefaultComponentStrategyFactory(InjectorFactory injectorFactory) {
        this.injectorFactory = injectorFactory;
    }

    @Override
    public <T> ComponentStrategy<T> create(Class<T> implementationType, List<ComponentInitializationListener> initializationListeners) {
        CompositeInjector<T> injector = injectorFactory.create(implementationType);
        Instantiator<T> instantiator = new DefaultInstantiator<T>(implementationType);
        if (implementationType.isAnnotationPresent(Prototype.class)) {
            return new PrototypeComponentStrategy<T>(injector, instantiator, initializationListeners);
        } else {
            return new SingletonComponentStrategy<T>(injector, instantiator, initializationListeners);
        }
    }

    @Override
    public <T> ComponentStrategy<T> createInstanceStrategy(T implementation, List<ComponentInitializationListener> initializationListeners) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) implementation.getClass();
        CompositeInjector<T> injector = injectorFactory.create(clazz);
        return new InstanceStrategy<T>(implementation, injector, initializationListeners);
    }

    @Override
    public <T> ComponentStrategy<T> createDecoratorStrategy(Class<T> implementationType, List<ComponentInitializationListener> initializationListeners, Class<?> delegateClass, ComponentStrategy<?> delegateStrategy) {
        CompositeInjector<T> injector = injectorFactory.create(implementationType);
        Instantiator<T> instantiator = new DecoratorInstantiator<T>(implementationType, delegateClass, delegateStrategy);
        if (implementationType.isAnnotationPresent(Prototype.class)) {
            return new PrototypeComponentStrategy<T>(injector, instantiator, initializationListeners);
        } else {
            return new SingletonComponentStrategy<T>(injector, instantiator, initializationListeners);
        }
    }
}
