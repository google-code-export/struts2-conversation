package com.github.overengineer.container.inject;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public interface InjectorFactory extends Serializable {
    <T> ComponentInjector<T> create(Class<T> implementationType);
    <T> MethodInjector<T> create(Class<T> injectionTarget, Method method, Class ... trailingArgs);
}
