package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.inject.CompositeInjector;
import com.github.overengineer.scope.container.inject.DefaultCompositeInjector;
import com.github.overengineer.scope.container.inject.Injector;
import com.github.overengineer.scope.container.instantiate.DecoratorInstantiator;
import com.github.overengineer.scope.container.instantiate.DefaultInstantiator;
import com.github.overengineer.scope.container.instantiate.Instantiator;

import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultComponentStrategyFactory implements ComponentStrategyFactory {

    @Override
    public <T> ComponentStrategy<T> create(Class<T> implementationType, List<ComponentInitializationListener> initializationListeners) {
        CompositeInjector<T> injector = new DefaultCompositeInjector<T>(Injector.CacheBuilder.build(implementationType));
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
        CompositeInjector<T> injector = new DefaultCompositeInjector<T>(Injector.CacheBuilder.build(clazz));
        return new InstanceStrategy<T>(implementation, injector, initializationListeners);
    }

    @Override
    public <T> ComponentStrategy<T> createDecoratorStrategy(Class<T> implementationType, List<ComponentInitializationListener> initializationListeners, Class<?> delegateClass, ComponentStrategy<?> delegateStrategy) {
        CompositeInjector<T> injector = new DefaultCompositeInjector<T>(Injector.CacheBuilder.build(implementationType));
        Instantiator<T> instantiator = new DecoratorInstantiator<T>(implementationType, delegateClass, delegateStrategy);
        if (implementationType.isAnnotationPresent(Prototype.class)) {
            return new PrototypeComponentStrategy<T>(injector, instantiator, initializationListeners);
        } else {
            return new SingletonComponentStrategy<T>(injector, instantiator, initializationListeners);
        }
    }
}
