package com.github.overengineer.container.proxy.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * @author rees.byars
 */
public class AdvisedInvocation<T> implements JoinPoint<T> {

    private final Iterator<Aspect> aspectIterator;
    private final T target;
    private final Method method;
    private final Object[] parameters;

    AdvisedInvocation(Iterator<Aspect> aspectIterator, T target, Method method, Object[] parameters) {
        this.aspectIterator = aspectIterator;
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
    public Object join() throws Throwable {
        if (aspectIterator.hasNext()) {
            return aspectIterator.next().advise(this);
        }
        try {
            return method.invoke(target, parameters);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

}
