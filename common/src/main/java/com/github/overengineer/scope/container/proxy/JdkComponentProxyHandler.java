package com.github.overengineer.scope.container.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 */
public class JdkComponentProxyHandler<T> implements ComponentProxyHandler<T>, InvocationHandler {

    private T component;
    private T proxy;

    @SuppressWarnings("unchecked")
    public JdkComponentProxyHandler(Class[] interfaces) {
        this.proxy = (T) Proxy.newProxyInstance(ComponentProxyHandler.class.getClassLoader(), interfaces, this);
    }

    @Override
    public T getProxy() {
        return proxy;
    }

    @Override
    public void setComponent(T component) {
        this.component = component;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return method.invoke(component, objects);
    }
}
