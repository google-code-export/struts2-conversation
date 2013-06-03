package com.github.overengineer.container.module;

import com.github.overengineer.container.key.GenericKey;
import com.github.overengineer.container.scope.Scope;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface MutableMapping<T> extends Serializable {
    MutableMapping<T> forType(Class<? super T> targetClass);
    MutableMapping<T> forType(GenericKey<? super T> targetKey);
    MutableMapping<T> withScope(Scope scope);
    MutableMapping<T> withName(String name);
}
