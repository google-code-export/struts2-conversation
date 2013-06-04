package com.github.overengineer.container.module;

import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.scope.Scope;

import java.io.Serializable;
import java.util.List;

/**
 * @author rees.byars
 */
public interface Mapping<T> extends Serializable {
    Class<T> getImplementationType();
    List<Class<?>> getTargetClasses();
    List<Key> getTargetKeys();
    Scope getScope();
    String getName();
}
