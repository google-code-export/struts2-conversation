package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.ComponentStrategy;
import com.github.overengineer.container.Provider;

/**
 * @author rees.byars
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
