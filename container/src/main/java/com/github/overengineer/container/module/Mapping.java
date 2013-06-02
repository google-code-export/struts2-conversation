package com.github.overengineer.container.module;

import com.github.overengineer.container.key.SerializableKey;
import com.github.overengineer.container.metadata.Scope;

import java.io.Serializable;
import java.util.List;

/**
 * @author rees.byars
 */
public interface Mapping<T> extends Serializable {
    Class<T> getImplementationType();
    List<Class<?>> getTargetClasses();
    List<SerializableKey> getTargetKeys();
    Scope getScope();
    String getName();
}
