package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class ComponentParameterProxy<T> implements ParameterProxy<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentParameterProxy.class);

    private final Type type;

    public ComponentParameterProxy(Type type) {
        this.type = type;
    }

    @Override
    public T get(Provider provider) {
        T component = provider.get(type);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolving component parameter of type [{}] to component of type [{}]", type, component.getClass().getName());
        }
        return component;
    }

}
