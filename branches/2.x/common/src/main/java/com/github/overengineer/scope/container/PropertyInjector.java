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

    public void inject(final T component, final Provider provider) {
        try {
            Object value = provider.getProperty(type, name);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting property [{}] on component of type [{}] with value [{}]", name, component.getClass().getName(), value);
            }
            setter.invoke(component, value);
        } catch (Exception e) {
            throw new InjectionException("Could not set property [" + name  + "] of type [" + type.getName() + "] on component of type [" + component.getClass().getName() + "] using setter [" + setter.getName() + "]", e);
        }
    }

}
