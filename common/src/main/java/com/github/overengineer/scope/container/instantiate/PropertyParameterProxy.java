package com.github.overengineer.scope.container.instantiate;

import com.github.overengineer.scope.container.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class PropertyParameterProxy<T> implements ParameterProxy<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyParameterProxy.class);

    private Class<T> type;
    private String name;

    public PropertyParameterProxy(Class<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public T get(Provider provider) {
        T property = provider.getProperty(type, name);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolving property parameter [{}] to value [{}]", name, property);
        }
        return property;
    }
}
