package com.github.overengineer.scope.container;

import java.util.*;

/**
 */
public class BootstrapContainer implements Provider {

    protected Map<Class<?>, Class<?>> mappings = new HashMap<Class<?>, Class<?>>();
    protected Map<Class<?>, ComponentStrategy<?>> strategies = new HashMap<Class<?>, ComponentStrategy<?>>();
    protected Map<String, Object> properties = new HashMap<String, Object>();
    protected List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();
    protected ComponentStrategyFactory strategyFactory = new DefaultComponentStrategyFactory();

    @Override
    public <T> T get(Class<T> clazz) {
        Class<?> implementationType = mappings.get(clazz);
        if (implementationType == null) {
            throw new MissingDependencyException("No components of type [" + clazz.getName() + "] have been registered with the container");
        }
        @SuppressWarnings("unchecked")
        ComponentStrategy<T> strategy = (ComponentStrategy<T>) strategies.get(implementationType);
        return strategy.get(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(Class<T> clazz, String name) {
        Object property = properties.get(name);
        if (property == null) {
            throw new MissingDependencyException("No property of name [" + name + "] has been registered with the container");
        }
        return (T) property;
    }

    protected void addMapping(Class<?> type, Class<?> implementationType) {

        if (!type.isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + type.getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        Class<?> existing = mappings.get(type);
        if (existing != null) {
            strategies.put(implementationType, strategyFactory.createDecoratorStrategy(implementationType, initializationListeners, existing, strategies.get(existing)));
        } else {
            strategies.put(implementationType, strategyFactory.create(implementationType, initializationListeners));
        }

        mappings.put(type, implementationType);
    }

    protected void addMapping(Class<?> type, Object implementation) {

        if (!type.isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + type.getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        strategies.put(implementation.getClass(), strategyFactory.createInstanceStrategy(implementation, initializationListeners));
        mappings.put(type, implementation.getClass());
    }
}
