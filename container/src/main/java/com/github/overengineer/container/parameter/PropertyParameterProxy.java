package com.github.overengineer.container.parameter;

import com.github.overengineer.container.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rees.byars
 */
public class PropertyParameterProxy<T> implements ParameterProxy<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyParameterProxy.class);

    private final Class<T> type;
    private final String name;

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
