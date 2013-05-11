package com.github.overengineer.container.inject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.github.overengineer.container.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rees.byars
 */
public final class ComponentInjector<T> extends BaseInjector<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentInjector.class);

    public ComponentInjector(Method setter, Type type) {
        super(setter, type);
    }

    public void inject(T component, Provider provider) {
        try {
            Object dependency = provider.get(type);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting component of type [{}] on component of type [{}]", dependency.getClass().getName(), component.getClass().getName());
            }
            setter.invoke(component, dependency);
        } catch (Exception e) {
            throw new InjectionException("Could not set component of type [" + type + "] on component of type [" + component.getClass().getName() + "] using setter [" + setter.getName() + "]", e);
        }
    }

}
