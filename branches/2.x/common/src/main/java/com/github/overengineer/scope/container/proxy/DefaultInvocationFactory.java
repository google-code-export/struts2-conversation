package com.github.overengineer.scope.container.proxy;

import java.lang.reflect.Method;

/**
 */
public class DefaultInvocationFactory implements InvocationFactory {
    @Override
    public <T> Invocation<T> create(T target, Method method, Object[] parameters) {
        return new DefaultInvocation<T>(target, method, parameters);
    }
}
