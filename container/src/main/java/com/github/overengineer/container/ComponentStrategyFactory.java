package com.github.overengineer.container;

import com.github.overengineer.container.scope.Scope;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ComponentStrategyFactory extends Serializable {

    <T> ComponentStrategy<T> create(Class<T> implementationType, Scope scope);

    <T> ComponentStrategy<T> createInstanceStrategy(T implementation);

    <T> ComponentStrategy<T> createCustomStrategy(ComponentStrategy providerStrategy);

}
