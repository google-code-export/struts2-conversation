package com.github.overengineer.container.parameter;

import com.github.overengineer.container.Provider;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ParameterBuilder<T> extends Serializable {
    boolean isDecorator();
    Object[] buildParameters(Provider provider, Object[] providedArgs);
}
