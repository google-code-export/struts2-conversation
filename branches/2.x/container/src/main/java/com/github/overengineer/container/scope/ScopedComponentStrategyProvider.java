package com.github.overengineer.container.scope;

import com.github.overengineer.container.ComponentStrategy;

/**
 * @rees.byars
 */
public interface ScopedComponentStrategyProvider {
    <T> ComponentStrategy<T> get(Class<T> implementationType, Object qualifier, ComponentStrategy<T> delegateStrategy);
}
