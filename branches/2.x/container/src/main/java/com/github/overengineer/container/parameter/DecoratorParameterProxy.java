package com.github.overengineer.container.parameter;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.SelectionAdvisor;
import com.github.overengineer.container.key.SerializableKey;

/**
 * @author rees.byars
 */
public class DecoratorParameterProxy<T> implements ParameterProxy<T> {

    private final SerializableKey key;
    private final Class<?> injectionTarget;

    public DecoratorParameterProxy(SerializableKey key, Class<?> injectionTarget) {
        this.key = key;
        this.injectionTarget = injectionTarget;
    }

    @Override
    public T get(Provider provider) {
        return provider.get(key, new SelectionAdvisor() {
            @Override
            public boolean validSelection(Class<?> candidateClass) {
                return candidateClass != injectionTarget; //TODO this prevents self injection.  OK??
            }
        });
    }

}
