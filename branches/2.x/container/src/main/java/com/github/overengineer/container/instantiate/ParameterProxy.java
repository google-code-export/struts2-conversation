package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.Provider;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ParameterProxy<T> extends Serializable {

    T get(Provider provider);

}
