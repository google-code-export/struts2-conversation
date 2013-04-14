package com.github.overengineer.scope.container.proxy.aop;

import java.lang.reflect.Method;
import java.util.Iterator;

/**
 */
public class InterceptableInvocation<T> implements Invocation<T> {

    private final Iterator<Interceptor> interceptorIterator;
    private final T target;
    private final Method method;
    private final Object[] parameters;

    InterceptableInvocation(Iterator<Interceptor> interceptorIterator, T target, Method method, Object[] parameters) {
        this.interceptorIterator = interceptorIterator;
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

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke() throws Exception {
        if (interceptorIterator.hasNext()) {
            return interceptorIterator.next().intercept(this);
        }
        return method.invoke(target, parameters);
    }

}
