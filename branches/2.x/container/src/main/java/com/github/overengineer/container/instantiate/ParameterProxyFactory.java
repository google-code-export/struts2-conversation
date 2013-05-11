package com.github.overengineer.container.instantiate;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface ParameterProxyFactory extends Serializable {

    <T> ParameterProxy<T> create(Type type, Annotation[] annotations);

}
