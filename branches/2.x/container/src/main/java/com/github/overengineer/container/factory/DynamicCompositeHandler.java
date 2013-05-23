package com.github.overengineer.container.factory;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface DynamicCompositeHandler<T> extends Serializable {
    T getDynamicComposite();
    DynamicCompositeHandler<T> add(T component);
}
