package com.github.overengineer.scope.container;

import java.io.Serializable;

public interface InjectionContext<T> extends Serializable {

    T getSingletonComponent();

    T getPrototypeComponent();

    Class<? extends T> getImplementationType();

    Provider getContainer();

}
