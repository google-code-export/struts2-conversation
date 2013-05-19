package com.github.overengineer.container.parameter;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.key.SerializableKey;

/**
 * @author rees.byars
 */
public class ComponentParameterProxy<T> implements ParameterProxy<T> {

    private final SerializableKey key;

    public ComponentParameterProxy(SerializableKey key) {
        this.key = key;
    }

    @Override
    public T get(Provider provider) {
        return provider.get(key);
    }

}
