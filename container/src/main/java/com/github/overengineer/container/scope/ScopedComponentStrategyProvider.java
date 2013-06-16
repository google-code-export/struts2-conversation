package com.github.overengineer.container.scope;

import com.github.overengineer.container.ComponentStrategy;

import java.io.Serializable;

/**
 * @rees.byars
 */
public interface ScopedComponentStrategyProvider extends Serializable {
    <T> ComponentStrategy<T> get(Class<T> implementationType, Object qualifier, ComponentStrategy<T> delegateStrategy);
}
