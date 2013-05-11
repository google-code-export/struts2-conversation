package com.github.overengineer.container.inject;

import java.io.Serializable;

import com.github.overengineer.container.Provider;

/**
 * @author rees.byars
 */
public interface Injector<T> extends Serializable {

    void inject(T component, Provider provider);

}
