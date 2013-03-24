package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.Prototype;
import com.github.overengineer.scope.container.ScopeContainer;

import java.io.Serializable;

/**
 */
public interface Instantiator<T> extends Serializable {

    Class<T> getTargetType();
    T getInstance(ScopeContainer container);

    class Factory {
        public static <T> Instantiator<T> create(Class<T> type) {
            if (type.isAnnotationPresent(Prototype.class)) {
                return new PrototypeInstantiator<T>(type);
            } else {
                return new SingletonInstantiator<T>(type);
            }
        }
        public static <T> Instantiator<T> wrap(T instance) {
            return new FalseInstantiator<T>(instance);
        }
    }
}
