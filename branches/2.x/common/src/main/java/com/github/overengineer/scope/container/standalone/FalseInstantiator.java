package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.ScopeContainer;

/**
 */
public class FalseInstantiator<T> implements Instantiator<T> {

    T instance;

    public FalseInstantiator(T instance) {
        this.instance = instance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getTargetType() {
        return (Class<T>) instance.getClass();
    }

    @Override
    public T getInstance(ScopeContainer container) {
        return instance;
    }
}
