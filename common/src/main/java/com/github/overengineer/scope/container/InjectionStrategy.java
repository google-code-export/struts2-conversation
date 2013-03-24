package com.github.overengineer.scope.container;

import java.io.Serializable;

public interface InjectionStrategy<T> extends Serializable {

    T getComponent();

    class Factory {
        public static <T> InjectionStrategy<T> create(InjectionContext<T> context) {
            if (context.getImplementationType().isAnnotationPresent(Prototype.class)) {
                return new PrototypeInjectionStrategy<T>(context);
            } else {
                return new SingletonInjectionStrategy<T>(context);
            }
        }
    }

}
