package com.github.overengineer.container.inject;

import com.github.overengineer.container.Provider;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface MethodInjector<T> extends Serializable {
    Object inject(T component, Provider provider);
}
