package com.github.overengineer.scope.container.instantiate;

import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.Provider;

/**
 */
public class DelegateProxy<T> implements ParameterProxy<T> {

    private final ComponentStrategy<T> strategy;

    public DelegateProxy(ComponentStrategy<T> strategy) {
        this.strategy = strategy;
    }

    @Override
    public T get(Provider provider) {
        return strategy.get(provider);
    }

}
