package com.github.overengineer.container.proxy.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public interface JoinPointInvocationFactory extends Serializable {

    <T> JoinPoint<T> create(T target, Method method, Object[] parameters);

}
