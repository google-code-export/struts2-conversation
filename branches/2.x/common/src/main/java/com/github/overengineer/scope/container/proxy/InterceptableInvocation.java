package com.github.overengineer.scope.container.proxy;

import java.lang.reflect.Method;
import java.util.Iterator;

/**
 */
public class InterceptableInvocation<T> extends DefaultInvocation<T> {

    private Iterator<Interceptor<T>> interceptorIterator;

    InterceptableInvocation(Iterator<Interceptor<T>> interceptorIterator, T target, Method method, Object[] parameters) {
        super(target, method, parameters);
        this.interceptorIterator = interceptorIterator;
    }

    @Override
    public Object invoke() throws Exception {
        if (interceptorIterator.hasNext()) {
            return interceptorIterator.next().intercept(this);
        }
        return super.invoke();
    }

}
