package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.Provider;

/**
 */
public class ComponentParameterProxy<T> implements ParameterProxy<T> {

    private Class<T> type;

    public ComponentParameterProxy(Class<T> type) {
        this.type = type;
    }

    @Override
    public T get(Provider provider) {
        return provider.get(type);
    }

}
