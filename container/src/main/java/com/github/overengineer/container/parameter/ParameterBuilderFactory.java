package com.github.overengineer.container.parameter;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public interface ParameterBuilderFactory extends Serializable {

    <T> ParameterBuilder<T> create(Class<T> injectionTarget, Constructor<T> constructor, Class[] trailingArgTypes);
    <T> ParameterBuilder<T> create(Class<T> injectionTarget, Method method, Class[] trailingArgTypes);

}
