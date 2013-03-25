package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.ScopeContainer;

/**
 */
public class ComponentParameterProxy<T> implements ParameterProxy<T> {

    private Class<T> type;

    public ComponentParameterProxy(Class<T> type) {
        this.type = type;
    }

    @Override
    public T get(ScopeContainer container) {
        return container.getComponent(type);
    }

}
