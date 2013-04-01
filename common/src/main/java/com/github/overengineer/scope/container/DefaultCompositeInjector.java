package com.github.overengineer.scope.container;

import java.util.Set;

/**
 */
public class DefaultCompositeInjector<T> implements CompositeInjector<T> {

    private Set<Injector<T>> injectors;

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
