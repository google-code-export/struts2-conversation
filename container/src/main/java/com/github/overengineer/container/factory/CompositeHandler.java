package com.github.overengineer.container.factory;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface CompositeHandler<T> extends Serializable {
    T getComposite();
    CompositeHandler<T> add(T component);
}
