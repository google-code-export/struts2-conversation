package com.github.overengineer.container.inject;

import java.lang.reflect.Method;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.key.SerializableKey;

/**
 * @author rees.byars
 */
public final class ComponentInjector<T> extends BaseInjector<T> {

    private final SerializableKey key;   //TODO test order - bet it breaks if key not already regsiterd

    public ComponentInjector(Method setter, Class targetClass, SerializableKey key) {
        super(setter, targetClass);
        this.key = key;
    }

    public void inject(T component, Provider provider) {
        try {
            Object dependency = provider.get(key);
            setter.invoke(component, dependency);
        } catch (Exception e) {
            throw new InjectionException("Could not set component of type [" + key.getType() + "] on component of type [" + component.getClass().getName() + "] using setter [" + setter.getName() + "]", e);
        }
    }

}
