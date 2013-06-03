package com.github.overengineer.container.instantiate;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface InstantiatorFactory extends Serializable {
    <T> Instantiator<T> create(Class<T> implementationType, Class ... trailingParamTypes);
}
