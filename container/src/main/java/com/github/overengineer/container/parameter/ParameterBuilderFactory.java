package com.github.overengineer.container.parameter;

import com.github.overengineer.container.util.ParameterizedFunction;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public interface ParameterBuilderFactory extends Serializable {

    <T> ParameterBuilder<T> create(Class<T> injectionTarget, Constructor<T> constructor, Class[] providedArgs);
    <T> ParameterBuilder<T> create(Class<T> injectionTarget, Method method, Class[] providedArgs);
    <T> ParameterBuilder<T> create(Class<?> injectionTarget, ParameterizedFunction parameterizedFunction, Class[] providedArgs);

}
