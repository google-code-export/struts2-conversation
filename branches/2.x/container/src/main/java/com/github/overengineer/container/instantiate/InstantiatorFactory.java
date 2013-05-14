package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.ComponentStrategy;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface InstantiatorFactory extends Serializable {
    <T> Instantiator<T> create(Class<T> implementationType, Class ... trailingParamTypes);
    <T> Instantiator<T> create(Class<T> implementationType, Class<?> delegateClass, ComponentStrategy<?> delegateStrategy);
}
