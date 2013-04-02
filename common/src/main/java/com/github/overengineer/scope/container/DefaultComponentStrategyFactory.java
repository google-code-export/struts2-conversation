package com.github.overengineer.scope.container;

/**
 */
public class DefaultComponentStrategyFactory implements ComponentStrategyFactory {
    @Override
    public <T> ComponentStrategy<T> create(Class<T> implementationType) {
        CompositeInjector<T> injector = new DefaultCompositeInjector<T>(Injector.CacheBuilder.build(implementationType));
        DefaultInstantiator<T> instantiator = new DefaultInstantiator<T>(implementationType);
        if (implementationType.isAnnotationPresent(Prototype.class)) {
            return new PrototypeComponentStrategy<T>(injector, instantiator);
        } else {
            return new SingletonComponentStrategy<T>(injector, instantiator);
        }
    }

    @Override
    public <T> ComponentStrategy<T> create(T implementation) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) implementation.getClass();
        CompositeInjector<T> injector = new DefaultCompositeInjector<T>(Injector.CacheBuilder.build(clazz));
        return new InstanceStrategy<T>(implementation, injector);
    }
}
