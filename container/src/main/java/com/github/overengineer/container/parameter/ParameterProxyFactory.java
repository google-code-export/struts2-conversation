package com.github.overengineer.container.parameter;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface ParameterProxyFactory extends Serializable {

    <T> ParameterProxy<T> create(Type type, Annotation[] annotations);
    ParameterProxy[] create(Method method);

}
