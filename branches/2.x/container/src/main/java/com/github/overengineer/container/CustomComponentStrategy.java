package com.github.overengineer.container;

import com.github.overengineer.container.inject.MethodInjector;

/**
 * @author rees.byars
 */
public class CustomComponentStrategy<T> implements ComponentStrategy<T> {

    private final ComponentStrategy providerStrategy;
    private final MethodInjector methodInjector;
    private final Class providedType;

    public CustomComponentStrategy(ComponentStrategy providerStrategy, MethodInjector methodInjector, Class providedType) {
        this.providerStrategy = providerStrategy;
        this.methodInjector = methodInjector;
        this.providedType = providedType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(Provider provider) {
        return (T) methodInjector.inject(providerStrategy.get(provider), provider);
    }

    @Override
    public Class getComponentType() {
        return providedType;
    }
}
