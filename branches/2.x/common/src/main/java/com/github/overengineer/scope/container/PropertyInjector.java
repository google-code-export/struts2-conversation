package com.github.overengineer.scope.container;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyInjector<T> extends BaseInjector<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyInjector.class);

    private final String name;

    public PropertyInjector(Method setter, String name, Class<?> type) {
        super(setter, type);
        this.name = name;
    }

    public void inject(final T component, final Provider container) {
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
