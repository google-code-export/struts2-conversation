package com.github.overengineer.scope.container;

import java.util.Set;

public class PrototypeInjectionStrategy<T> implements InjectionStrategy<T> {

    private Set<Injector<T>> injectors;
    private InjectionContext<T> injectionContext;

    public PrototypeInjectionStrategy(InjectionContext<T> injectionContext) {
        injectors = Injector.CacheBuilder.build(injectionContext.getImplementationType());
        this.injectionContext = injectionContext;
    }

    @Override
    public T getComponent() {
        T component = injectionContext.getPrototypeComponent();
        for (Injector<T> injector : injectors) {
            injector.inject(component, injectionContext.getContainer());
        }
        if (component instanceof PostConstructable) {
            ((PostConstructable) component).init();
        }
        return component;
    }

}
