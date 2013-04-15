package com.github.overengineer.scope.container.proxy.aop;

import java.lang.reflect.Method;

/**
 */
public interface InvocationFactory {
    <T> JoinPointInvocation<T> create(T target, Method method, Object[] parameters);
}
