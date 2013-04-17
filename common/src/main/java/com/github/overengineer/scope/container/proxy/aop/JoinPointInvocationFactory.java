package com.github.overengineer.scope.container.proxy.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public interface JoinPointInvocationFactory extends Serializable {

    <T> JoinPointInvocation<T> create(T target, Method method, Object[] parameters);

}
