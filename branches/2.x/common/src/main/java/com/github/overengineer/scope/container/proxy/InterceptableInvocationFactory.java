package com.github.overengineer.scope.container.proxy;

import java.lang.reflect.Method;

/**
 */
public class InterceptableInvocationFactory  implements InvocationFactory {
    @Override
    public <T> Invocation<T> create(T target, Method method, Object[] parameters) {
        //TODO
        return null;
    }
}
