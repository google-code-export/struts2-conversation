package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.Provider;

/**
 */
public class PropertyParameterProxy<T> implements ParameterProxy<T> {

    private Class<T> type;
    private String name;

    public PropertyParameterProxy(Class<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public T get(Provider provider) {
        return provider.getProperty(type, name);
    }
}
