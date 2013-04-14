package com.github.overengineer.scope.container;

import java.io.Serializable;
import java.util.List;

/**
 */
public interface ComponentStrategyFactory extends Serializable {

    <T> ComponentStrategy<T> create(Class<T> implementationType, List<ComponentInitializationListener> initializationListeners);

    <T> ComponentStrategy<T> createInstanceStrategy(T implementation, List<ComponentInitializationListener> initializationListeners);

    <T> ComponentStrategy<T> createDecoratorStrategy(Class<T> implementationType, List<ComponentInitializationListener> initializationListeners, Class<?> delegateClass, ComponentStrategy<?> delegateStrategy);

}
