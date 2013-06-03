package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.Provider;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface Instantiator<T> extends Serializable {

    boolean isDecorator();

    T getInstance(Provider provider, Object ... trailingParams);

    Class getProducedType();

}
