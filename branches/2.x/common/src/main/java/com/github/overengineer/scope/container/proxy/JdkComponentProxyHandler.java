package com.github.overengineer.scope.container.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class JdkComponentProxyHandler<T> implements ComponentProxyHandler<T>, InvocationHandler {

    protected T component;
    private final T proxy;

    @SuppressWarnings("unchecked")
    public JdkComponentProxyHandler(JdkProxyFactory factory) {
        this.proxy = (T) factory.newProxyInstance(this);
    }

    @Override
    public T getProxy() {
        return proxy;
    }

    @Override
    public T getComponent() {
        return component;
    }

    @Override
    public void setComponent(T component) {
        this.component = component;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] parameters) throws Throwable {
        try {
            return method.invoke(component, parameters);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

}
