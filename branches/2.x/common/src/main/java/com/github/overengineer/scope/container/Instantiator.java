package com.github.overengineer.scope.container;

import java.io.Serializable;

/**
 */
public interface Instantiator<T> extends Serializable {

    T getInstance(Provider provider);

}
