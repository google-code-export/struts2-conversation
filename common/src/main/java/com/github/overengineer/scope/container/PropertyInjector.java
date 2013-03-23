package com.github.overengineer.scope.container;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyInjector<T> implements Injector<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyInjector.class);

    private final Method setter;
    private final String name;
    private final Class<?> type;

    public PropertyInjector(final Method setter, final String name, final Class<?> type) {
        this.setter = setter;
        this.name = name;
        this.type = type;
    }

    public void inject(final T component, final ScopeContainer container) {
        try {
            Object value = container.getProperty(type, name);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting property [{}] on component of type [{}] with value [{}]", name, component.getClass().getName(), value);
            }
            setter.invoke(component, value);
        } catch (Exception e) {
            LOG.error("Could not set property [{}] of type [{}] on component of type [{}] using setter [{}]", name, type.getName(), component.getClass().getName(), setter.getName(), e);
        }
    }

}
