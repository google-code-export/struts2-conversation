package com.github.overengineer.scope.container;

public interface InjectionContext<T> {
    T getSingletonComponent();

    T getPrototypeComponent();

    Class<? extends T> getImplementationType();

    ScopeContainer getContainer();
}
