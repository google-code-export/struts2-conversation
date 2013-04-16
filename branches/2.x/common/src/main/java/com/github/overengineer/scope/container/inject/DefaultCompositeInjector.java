package com.github.overengineer.scope.container.inject;

import com.github.overengineer.scope.container.Provider;

import java.util.Set;

/**
 */
public class DefaultCompositeInjector<T> implements CompositeInjector<T> {

    private final Set<Injector<T>> injectors;

    public DefaultCompositeInjector(Set<Injector<T>> injectors) {
         this.injectors = injectors;
    }

    @Override
    public void inject(T component, Provider provider) {
        for (Injector<T> injector : injectors) {
            injector.inject(component, provider);
        }
    }
}
