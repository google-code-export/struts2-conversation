package com.github.overengineer.container;

import com.github.overengineer.container.scope.Scope;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ComponentStrategyFactory extends Serializable {

    <T> ComponentStrategy<T> create(Class<T> implementationType, Object qualifier, Scope scope);

    <T> ComponentStrategy<T> createInstanceStrategy(T implementation, Object qualifier);

    <T> ComponentStrategy<T> createCustomStrategy(ComponentStrategy providerStrategy, Object qualifier);

}
