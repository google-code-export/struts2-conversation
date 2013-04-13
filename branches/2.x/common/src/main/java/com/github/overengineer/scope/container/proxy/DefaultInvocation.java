package com.github.overengineer.scope.container.proxy;

import java.lang.reflect.Method;
/**
 */
public class DefaultInvocation<T> implements Invocation<T> {

    private T target;
    private Method method;
    private Object[] parameters;

    DefaultInvocation(T target, Method method, Object[] parameters) {
        this.target = target;
        this.method = method;
        this.parameters = parameters;
    }

    @Override
    public T getTarget() {
        return target;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object invoke() throws Exception {
        return method.invoke(target, parameters);
    }
}
