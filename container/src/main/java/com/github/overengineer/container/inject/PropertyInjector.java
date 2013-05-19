package com.github.overengineer.container.inject;

import java.lang.reflect.Method;

import com.github.overengineer.container.Provider;

/**
 * @author rees.byars
 */
public final class PropertyInjector<T> extends BaseInjector<T> {

    private final String name;

    public PropertyInjector(Method setter, String name, Class parameterType) {
        super(setter, parameterType);
        this.name = name;
    }

    public void inject(final T component, final Provider provider) {
        try {
            Object value = provider.getProperty(parameterType, name);
            getSetter().invoke(component, value);
        } catch (Exception e) {
            throw new InjectionException("Could not set property [" + name  + "] of type [" + parameterType + "] on component of type [" + component.getClass().getName() + "] using setter [" + getSetter().getName() + "]", e);
        }
    }

}
