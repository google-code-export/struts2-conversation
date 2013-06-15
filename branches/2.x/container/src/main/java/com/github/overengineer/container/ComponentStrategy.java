package com.github.overengineer.container;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ComponentStrategy<T> extends Serializable {

    T get(Provider provider);

    Class getComponentType();

    boolean isDecorator();

    Object getQualifier();

}
