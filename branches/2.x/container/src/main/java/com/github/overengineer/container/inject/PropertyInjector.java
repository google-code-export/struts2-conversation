package com.github.overengineer.container.inject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.github.overengineer.container.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rees.byars
 */
public final class PropertyInjector<T> extends BaseInjector<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyInjector.class);

    private final String name;

    public PropertyInjector(Method setter, String name, Type type) {
        super(setter, type);
        this.name = name;
    }

    public void inject(final T component, final Provider provider) {
        try {
            Object value = provider.getProperty(parameterClass, name);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting property [{}] on component of type [{}] with value [{}]", name, component.getClass().getName(), value);
            }
            setter.invoke(component, value);
        } catch (Exception e) {
            throw new InjectionException("Could not set property [" + name  + "] of type [" + type + "] on component of type [" + component.getClass().getName() + "] using setter [" + setter.getName() + "]", e);
        }
    }

}
