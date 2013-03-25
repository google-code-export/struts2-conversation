package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.ScopeContainer;

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
    public T get(ScopeContainer container) {
        return container.getProperty(type, name);
    }
}
