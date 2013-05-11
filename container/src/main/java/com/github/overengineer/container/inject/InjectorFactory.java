package com.github.overengineer.container.inject;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface InjectorFactory extends Serializable {
    <T> CompositeInjector<T> create(Class<T> implementationType);
}
