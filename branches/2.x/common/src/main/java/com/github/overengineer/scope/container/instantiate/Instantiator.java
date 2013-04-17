package com.github.overengineer.scope.container.instantiate;

import com.github.overengineer.scope.container.Provider;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface Instantiator<T> extends Serializable {

    T getInstance(Provider provider);

}
