package com.github.overengineer.scope.container.instantiate;

import com.github.overengineer.scope.container.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rees.byars
 */
public class ComponentParameterProxy<T> implements ParameterProxy<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentParameterProxy.class);

    private final Class<T> type;

    public ComponentParameterProxy(Class<T> type) {
        this.type = type;
    }

    @Override
    public T get(Provider provider) {
        T component = provider.get(type);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolving component parameter of type [{}] to component of type [{}]", type.getName(), component.getClass().getName());
        }
        return component;
    }

}
