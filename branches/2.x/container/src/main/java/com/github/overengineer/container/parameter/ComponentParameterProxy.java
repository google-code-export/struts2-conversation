package com.github.overengineer.container.parameter;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.key.Key;

/**
 * @author rees.byars
 */
public class ComponentParameterProxy<T> implements ParameterProxy<T> {

    private final Key<T> key;

    public ComponentParameterProxy(Key<T> key) {
        this.key = key;
    }

    @Override
    public T get(Provider provider) {
        return provider.get(key);
    }

}
