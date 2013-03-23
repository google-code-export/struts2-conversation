package com.github.overengineer.scope.container;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComponentInjector<T> implements Injector<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentInjector.class);

    private final Method setter;
    private final Class<?> type;

    public ComponentInjector(final Method setter, final Class<?> type) {
        this.setter = setter;
        this.type = type;
    }

    public void inject(final T component, final ScopeContainer container) {
        try {
            Object dependency = container.getComponent(type);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting component of type [{}] on component of type [{}]", dependency.getClass().getName(), component.getClass().getName());
            }
            setter.invoke(component, dependency);
        } catch (Exception e) {
            LOG.error("Could not set component of type [{}] on component of type [{}] using setter [{}]", type.getName(), component.getClass().getName(), setter.getName(), e);
        }
    }

}
